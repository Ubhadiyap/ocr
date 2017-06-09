package com.eneo.ocr;

import com.eneo.ocr.Model.MyShortcuts;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import android.support.annotation.NonNull;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import static com.eneo.ocr.BaseActivity.REQUEST_STORAGE_WRITE_ACCESS_PERMISSION;

public class TestCrop extends Activity {
    private CameraCharacteristics k;
    private ImageView resultView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    AlertDialog mAlertDialog;
    static Context ctx;
    Rect r = new Rect(0, 0, 300, 40);

    private static final String TAG = "TestCrop";

    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Button takePictureButton;
    CameraPermissionsListener listener;
    private static final int REQUEST_PERMISSIONS_CAMERA = 1;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("mixed_sample");

//                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public TestCrop() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main);
        resultView = (ImageView) findViewById(R.id.result_image);
        resultView.setImageDrawable(null);
        ctx = this;

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.e(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
//        Crop.pickImage(this);
//        TakePictureIntent();
        Box box = new Box(this);
        addContentView(box, new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT));
        int fin = Integer.parseInt(MyShortcuts.getDefaults("width", ctx)) + Integer.parseInt(MyShortcuts.getDefaults("width", ctx)) / 3;
        int height = Integer.parseInt(MyShortcuts.getDefaults("height", ctx)) / 4;
        int top = height / 4;
        int width = Integer.parseInt(MyShortcuts.getDefaults("width", ctx));
        int height2 = Integer.parseInt(MyShortcuts.getDefaults("height", ctx));
        Log.e("height_", Integer.parseInt(MyShortcuts.getDefaults("height", ctx)) + "");
        int left = width - (width - 150);
        Log.e("left", left + "");
        int tops = height2 - fin;
        Log.e("top", tops + "");
        int right = width - 150;
        Log.e("right", right + "");
        int bottom = height + top;
        Log.e("bottom", bottom + "");


        DisplayMetrics dm = new DisplayMetrics();
        try {
            getWindowManager().getDefaultDisplay().getMetrics(dm);
        } catch (Exception ex) {
        }
        int widthOfscreen = dm.widthPixels;
        int heightOfScreen = dm.heightPixels;

        Log.e("widthp", widthOfscreen + "");
        Log.e("heightp", heightOfScreen + "");



//     TODO Draw the exterior to be blur
      /*
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);*/
//        TODO New Code
//        textureView = new TextureView(this);
        textureView = (TextureView) findViewById(R.id.texture);
        Camera camera = null;
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        startBackgroundThread();
        if (textureView.isAvailable()) {
            Log.e("TV", "is not null");
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
            Log.e("TV", "is null");

        }

        takePictureButton = (Button) findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        String path= Environment.getExternalStorageDirectory()+"/EneoCV/";
        File file = new File(path);

        if (!file.exists()){
            file.mkdirs();
            saveInternal("images.xml",path);
            saveInternal("classifications.xml",path);
        }


        File file2 = new File(path);

        if (!file2.exists()){
            file2.mkdirs();
            saveInternal("classifications.xml",path);
        }

//        copyFileOrDir("classifications.xml");
    }

    private void saveInternal(String name,String path){
            AssetManager assetManager = getBaseContext().getAssets();
            try {
                InputStream in = assetManager.open(name);
                OutputStream out = new FileOutputStream(path+name);
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
            } catch (Exception e) {
                e.getMessage();
            }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select) {
            resultView.setImageDrawable(null);
            TakePictureIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
//            beginCrop(result.getData());
            startCropActivity(result.getData());
        } else if (requestCode == UCrop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
           /* Bundle extras = result.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            Crop.pickImage(this);
            saveImage(result.getData());*/
            startCropActivity(result.getData());
            MyShortcuts.showToast("Photo saved", getBaseContext());
//            mImageView.setImageBitmap(imageBitmap);

        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(TestCrop.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
//            resultView.setImageURI(UCrop.getOutput(result));
            ResultActivity.startWithUri(TestCrop.this, UCrop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(TestCrop.this, "Cannot retrieve the cropped image", Toast.LENGTH_SHORT).show();

        }
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            resultView.setImageURI(Crop.getOutput(result));
//            ResultActivity.startWithUri(TestCrop.this, resultUri);
        } else {
            Toast.makeText(TestCrop.this, "Cannot retrieve the cropped image", Toast.LENGTH_SHORT).show();
        }
    }


    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = "eneo_meter";

        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));


       /* uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);*/

        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);

        uCrop.start(TestCrop.this);
    }

    /*private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void TakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.yalantis.ucrop.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private void saveImage(Uri uri) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(R.string.permission_write_storage_rationale),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        } else {
            Uri imageUri = uri;
            if (imageUri != null && imageUri.getScheme().equals("file")) {
                try {
                    copyFileToDownloads(imageUri);
                } catch (Exception e) {
                    Toast.makeText(TestCrop.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("TestCrop", imageUri.toString(), e);
                }
            } else {
                Toast.makeText(TestCrop.this, "Unexpected error occured", Toast.LENGTH_SHORT).show();
            }
        }

//        TODO open a
    }

    private void copyFileToDownloads(Uri croppedFileUri) throws Exception {
        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "Eneo_Before";
        String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());

        File eneoDir = new File(downloadsDirectoryPath);
        if (!eneoDir.exists()) {
            eneoDir.mkdir();
        }


        File saveFile = new File(eneoDir, filename);

        Log.e("downloadfolder", downloadsDirectoryPath);
        Log.e("filename", filename);

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

    }

    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(TestCrop.this,
                                    new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }


//    public void openCameraReportError() {
//        Object var1 = c;
//        synchronized (c) {
//            if (this.f == null) {
//                Log.d(b, "Skip camera opening: No surface texture exists.");
//            } else if (this.g != null) {
//                Log.d(b, "Skip camera opening: A camera is already open.");
//            } else if (!this.h.hasQueuedThreads()) {
//                try {
//                    final CameraController2 var4 = this;
//                    Log.d(b, "open camera");
//                    if (this.l == null) {
//                        this.l = new HandlerThread("CameraBackground");
//                        this.l.start();
//                        this.m = new Handler(this.l.getLooper());
//                    }
//
//                    ViewGroup var5 = (ViewGroup) this.previewView.getParent();
//                    CameraManager var6 = (CameraManager) this.d.getSystemService("camera");
//                    this.k = null;
//                    StreamConfigurationMap var7 = null;
//                    String[] var8;
//                    int var9 = (var8 = var6.getCameraIdList()).length;
//
//                    for (int var10 = 0; var10 < var9; ++var10) {
//                        String var11 = var8[var10];
//                        var4.k = var6.getCameraCharacteristics(var11);
//                        Integer var12;
//                        if (((var12 = (Integer) var4.k.get(CameraCharacteristics.LENS_FACING)) == null || var12.intValue() != 0) && (var7 = (StreamConfigurationMap) var4.k.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)) != null) {
//                            var4.j = var11;
//                            break;
//                        }
//                    }
//
//                    if (var4.j == null || var4.k == null || var7 == null) {
//                        throw new Exception("No back facing camera found");
//                    }
//
//                    var4.cameraFeatures = new CameraFeatures2(var4.j, var4.k);
//                    var4.n = CameraUtil.getCameraDisplayOrientation(var4.d, var4.k);
//                    var4.cameraConfig = new CameraConfig(var4.d, var4.preferredCameraConfig, var4.cameraFeatures, var5.getWidth(), var5.getHeight());
//                    var4.v = var4.cameraConfig.getFrameSize().getWidth();
//                    var4.w = var4.cameraConfig.getFrameSize().getHeight();
//                    var4.previewView.post(new Runnable() {
//                        public final void run() {
//                            var4.previewView.getParent().requestLayout();
//                        }
//                    });
//                    var4.e();
//                    if (!var4.h.tryAcquire(2500L, TimeUnit.MILLISECONDS)) {
//                        throw new RuntimeException("Time out waiting to lock camera opening.");
//                    }
//
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return;
//                    }
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        var6.openCamera(var4.j, var4.B, var4.m);
//                    }
//                } catch (Exception var14) {
//                    Log.d(b, "Error initializing camera: " + var14.getMessage());
//                    this.reportCameraError(var14);
//                }
//
//            }
//        }
//    }

//    TODO Getting the display dimensions
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    final void a(@NonNull Context var1, @NonNull RectF var2, float var3, float var4) {
//
//            Rect var5;
//        var5 = (Rect) k.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
//        int var6 = CameraUtil.getCameraDisplayOrientation(var1, this.k);
//            (new StringBuilder("SENSOR_INFO_ACTIVE_ARRAY_SIZE: (x:")).append(var5.left).append(", y:").append(var5.top).append(", w:").append(var5.width()).append(", h:").append(var5.height()).append(")");
//            this.z = CameraUtil.a(var5, var6, var2, var3, var4);
//            (new StringBuilder("Calculated MeteringRectangle: ")).append(this.z);
//            if(this.z != null) {
//                MeteringRectangle[] var8 = new MeteringRectangle[]{this.z};
//                if(this.cameraConfig.isUpdateRegionsOnAutoFocusEnabled()) {
//                    MeteringRectangle var9 = new MeteringRectangle(this.z.getX(), this.z.getY(), this.z.getWidth(), this.z.getHeight() + this.A++ % 2, this.z.getMeteringWeight());
//                    var8[0] = var9;
//                }
//
//                if(this.cameraConfig.isFocusRegionEnabled()) {
//                    this.i.set(CaptureRequest.CONTROL_AF_REGIONS, var8);
//                } else {
//                    this.i.set(CaptureRequest.CONTROL_AF_REGIONS, (Object)null);
//                }
//
//                if(this.cameraConfig.isAutoExposureRegionEnabled()) {
//                    this.i.set(CaptureRequest.CONTROL_AE_REGIONS, var8);
//                } else {
//                    this.i.set(CaptureRequest.CONTROL_AE_REGIONS, (Object)null);
//                }
//
//                CameraController2 var10 = this;
//                if(this.o != null) {
//                    try {
//                        var10.c();
//                        return;
//                    } catch (CameraAccessException var12) {
//                        this.reportCameraError(var12);
//                    }
//                }
//            }
//
//
//    }

//    TODO Following tutorial to start camera

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    //    TODO CameraCaptureSession not used now
   /* final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(TestCrop.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };*/
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBackgroundThread.quitSafely();
        }
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 1080;
            int height = 1845;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

//            TODO set up my image width and height here
            Log.e("hi", height + "");
            Rect zoomCropPreview = new Rect(1094, 822, 2186, 1642); //(1092x820, 4:3 aspect ratio)

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
//            TODO new code
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//            captureBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomCropPreview);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getExternalStorageDirectory() + "/cropped_pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                readerListener = new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = null;
//                        image = reader.acquireLatestImage();
//                        byte[] bytes = YUV_420_888toCroppedY(image);
//                        try {
//                            save(bytes);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                image = reader.acquireLatestImage();
                            }
                            ByteBuffer buffer = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                buffer = image.getPlanes()[0].getBuffer();
                            }
                            byte[] bytes = new byte[buffer.capacity()];
                            buffer.get(bytes);

                            Rect frame = new Rect(350, 50, 450, 150);
                            Rect frame2 = new Rect(150, 405, 930, 576);

                            Matrix matrix = new Matrix();

                            matrix.postRotate(90);

                            int[] pixels = new int[780 * 4128];//the size of the array is the dimensions of the sub-photo
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Log.e("width bit", bitmap.getWidth() + "");
                            Log.e("x", bitmap.getWidth()/2 - bitmap.getHeight()/2 + "");



                            bitmap = Bitmap.createBitmap(bitmap,bitmap.getWidth()/2 - bitmap.getHeight()/2, 0, bitmap.getHeight()/5, bitmap.getHeight(), matrix, true);
                            //                            createBitmap(Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter)

                            // TODO bitmap.getHeight() 3096 4128 wdth
                            // TODO       Width 1080 Height 1845/1920

                          /*  if (bitmap.getWidth() >= bitmap.getHeight()){

                                bitmap = Bitmap.createBitmap(
                                        bitmap,
                                        bitmap.getWidth()/2 - bitmap.getHeight()/2,
                                        0,
                                        bitmap.getHeight(),
                                        bitmap.getHeight()/3
                                );

                            }else{

                                bitmap = Bitmap.createBitmap(
                                        bitmap,
                                        0,
                                        bitmap.getHeight()/2 - bitmap.getWidth()/2,
                                        bitmap.getWidth(),
                                        bitmap.getWidth()
                                );
                            }
*/

//                            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 150, 405,171 , 780);//the stride value is (in my case) the width value
//                            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 150, 405,780 , 780);//the stride value is (in my case) the width value
//                            bitmap = Bitmap.createBitmap(pixels, 0, bitmap.getWidth(), 171, 780, Bitmap.Config.ARGB_8888);//ARGB_8888 is a good quality configuration
//                            bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()/2 - bitmap.getWidth()/2,  bitmap.getWidth(), 780, matrix, true);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//100 is the best quality possibe
                            byte[] square = bos.toByteArray();

//                           TODO  Pass my data to opencv for processing
                            Tutorial2Activity.find(square);
//                            save(bytes);
                            save(square);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (image != null) {
                                image.close();
                            }
                        }
                    }

                    private void save(byte[] bytes) throws IOException {
                        OutputStream output = null;
                        try {
                            output = new FileOutputStream(file);
                            output.write(bytes);
                        } finally {
                            if (null != output) {
                                output.close();
                            }
                        }
                    }
                };
            }
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(TestCrop.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(TestCrop.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            /*
            * */
            String[] var8;
            String j;
            CameraCharacteristics charact;
            int var9 = (var8 = manager.getCameraIdList()).length;

           /* for(int var10 = 0; var10 < var9; ++var10) {
                String var11 = var8[var10];
                charact = manager.getCameraCharacteristics(var11);
                Integer var12;
                if(((var12 = (Integer)charact.get(CameraCharacteristics.LENS_FACING)) == null || var12.intValue() != 0) && (var7 = (StreamConfigurationMap)var4.k.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)) != null) {
                    j = var11;
                    break;
                }
            }*/

            /*
            * */

            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TestCrop.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }


            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
//TODO new code
        Rect zoomCropPreview = new Rect(1094, 822, 2186, 1642); //(1092x820, 4:3 aspect ratio)
//        captureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomCropPreview);


        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(TestCrop.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    //    TODO Metering Rectangle
    @TargetApi(21)
    static MeteringRectangle a(Rect var0, int var1, RectF var2, float var3, float var4) {
        float var5 = (float) var0.width() / var3;
        float var6 = (float) var0.height() / var4;
        if (var1 == 90 || var1 == 270) {
            var5 = (float) var0.height() / var3;
            var6 = (float) var0.width() / var4;
        }

        int var7;
        int var8;
        int var9;
        int var10;
        switch (var1) {
            case 0:
                var7 = (int) (var5 * var2.left);
                var8 = (int) (var5 * var2.right);
                var9 = (int) (var6 * var2.top);
                var10 = (int) (var6 * var2.bottom);
                break;
            case 90:
                var7 = (int) (var6 * var2.top);
                var8 = (int) (var6 * var2.bottom);
                var9 = (int) ((float) var0.height() - var5 * var2.right);
                var10 = (int) ((float) var0.height() - var5 * var2.left);
                break;
            case 180:
                var7 = (int) ((float) var0.width() - var5 * var2.right);
                var8 = (int) ((float) var0.width() - var5 * var2.left);
                var9 = (int) ((float) var0.height() - var6 * var2.bottom);
                var10 = (int) ((float) var0.height() - var6 * var2.top);
                break;
            case 270:
                var7 = (int) ((float) var0.width() - var6 * var2.bottom);
                var8 = (int) ((float) var0.width() - var6 * var2.top);
                var9 = (int) (var5 * var2.left);
                var10 = (int) (var5 * var2.right);
                break;
            default:
                throw new IllegalStateException("Unknown camera orientation while updating focus area!");
        }

        var7 += var0.left;
        var9 += var0.top;
        var8 += var0.left;
        var10 += var0.top;
        if (var7 < var8 && var9 < var10) {
            return new MeteringRectangle(var7, var9, var8 - var7, var10 - var9, 100);
        } else {
            throw new IllegalStateException("Focus area smaller or equal to zero!");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.e(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private int getCurrentOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switch (rotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static byte[] YUV_420_888toCroppedY(Image image) {
//        Integer.parseInt(MyShortcuts.getDefaults("width",ctx))
        int fin = Integer.parseInt(MyShortcuts.getDefaults("width", ctx)) + Integer.parseInt(MyShortcuts.getDefaults("width", ctx)) / 3;
        int height = Integer.parseInt(MyShortcuts.getDefaults("height", ctx)) / 4;
        int top = height / 4;
        int width = Integer.parseInt(MyShortcuts.getDefaults("width", ctx));
        int height2 = Integer.parseInt(MyShortcuts.getDefaults("height", ctx));


        Rect cropRect = new Rect(width - (width - 150), height2 - fin, width - 150, height + top);

        byte[] yData;

        Log.e("width_", Integer.parseInt(MyShortcuts.getDefaults("width", ctx)) + "");
        Log.e("height_", Integer.parseInt(MyShortcuts.getDefaults("height", ctx)) + "");
        int left = width - (width - 150);
        Log.e("left", left + "");
        int tops = height2 - fin;
        Log.e("top", tops + "");
        int right = width - 150;
        Log.e("right", right + "");
        int bottom = height + top;
        Log.e("bottom", bottom + "");


        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();

        int ySize = yBuffer.remaining();

        yData = new byte[ySize];

        yBuffer.get(yData, 0, ySize);

        if (cropRect != null) {

            int cropArea = (cropRect.right - cropRect.left) * (cropRect.bottom - cropRect.top);
            Log.e("crop area", cropArea + "");

            byte[] croppedY = new byte[cropArea];

            int cropIndex = 0;


//        TODO new code
            int yRowStride = image.getPlanes()[0].getRowStride();
            // from the top of the rectangle, to the bottom, sequentially add rows to the output array, croppedY
            Log.e("topcrop", cropRect.top + "");
            Log.e("cropheight", cropRect.height() + "");
            for (int y = cropRect.top; y < cropRect.top + cropRect.height(); y++) {


                int rowStart = y * yRowStride + cropRect.left;
                int rowEnd = y * yRowStride + cropRect.left + cropRect.width();
            /*// (2x+W) * y + x
            int rowStart = (2*cropRect.left + cropRect.width()) * y + cropRect.left;

            // (2x+W) * y + x + W
            int rowEnd = (2*cropRect.left + cropRect.width()) * y + cropRect.left + cropRect.width();
*/
                for (int x = rowStart; x < rowEnd; x++) {
                    croppedY[cropIndex] = yData[x];
                    cropIndex++;
                }
            }

            return croppedY;
        }

        return yData;
    }

    public interface CameraPermissionsListener{
        void onCameraPermissionsGranted(CameraManager manager);
    }

    private void initCameraPerms() {
        //check if we have permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //if we don't, request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSIONS_CAMERA);
        } else {
            //permissions have been granted
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            listener.onCameraPermissionsGranted(cameraManager);
        }
    }

    public static native void Detect(long mat);



}












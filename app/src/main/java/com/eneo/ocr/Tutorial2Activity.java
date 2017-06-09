package com.eneo.ocr;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfByte;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.android.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.eneo.ocr.Model.MyShortcuts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;

import android.net.Uri;

public class Tutorial2Activity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVDetect::Activity";

    private static final int VIEW_MODE_RGBA = 0;
    private static final int VIEW_MODE_GRAY = 1;
    private static final int VIEW_MODE_CANNY = 2;
    private static final int VIEW_MODE_FEATURES = 5;

    private int mViewMode = VIEW_MODE_CANNY;
    ;
    private Mat mRgba, mRgbaT, mRgbaF;
    private Mat mIntermediateMat;
    private Mat mGray;

    private MenuItem mItemPreviewRGBA;
    private MenuItem mItemPreviewGray;
    private MenuItem mItemPreviewCanny;
    private MenuItem mItemPreviewFeatures;
    private String image_name;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Bitmap bitmap;

    private Uri uri;

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

    public Tutorial2Activity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.e(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String filepath = getIntent().getStringExtra("filepath");
        Log.e("path", filepath);
        File file = new File(filepath);

        uri = getIntent().getData();
        InputStream image_stream = null;
        try {
            image_stream = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(image_stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MyShortcuts.showToast("did not find the image", getBaseContext());
        }


//        Utils.bitmapToMat(bitmap, mRgba);
        setContentView(R.layout.tutorial2_surface_view);
        mRgba = Imgcodecs.imread(filepath);

       /* mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial2_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);*/

        Button btn = (Button) findViewById(R.id.capture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_name = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "meter");
                write(image_name + ".png", mRgba);
                Mat mat = new Mat();
//                MyShortcuts.showToast(Environment.getExternalStorageDirectory().getAbsolutePath() + "/eneo/"+"test.jpg",getBaseContext());
                Mat m = Imgcodecs.imread(Environment.getExternalStorageDirectory().getAbsolutePath() + "/eneo/" + "test.png");
                Imgproc.Canny(m, mRgba, 80, 100);
                write(String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "canny") + ".png", mRgba);
                Process(mRgba.getNativeObjAddr(), mat.getNativeObjAddr());
                write(String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "processed") + ".png", mat);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemPreviewRGBA = menu.add("Preview RGBA");
        mItemPreviewGray = menu.add("Preview GRAY");
        mItemPreviewCanny = menu.add("Canny");
        mItemPreviewFeatures = menu.add("Find features");
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.e(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);

        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        final int viewMode = mViewMode;
        switch (viewMode) {
            case VIEW_MODE_GRAY:
                // input frame has gray scale format
                Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
            case VIEW_MODE_RGBA:
                // input frame has RBGA format
                mRgba = inputFrame.rgba();
                break;
            case VIEW_MODE_CANNY:
                // input frame has gray scale format
                mRgba = inputFrame.rgba();
               /* Core.transpose(mRgba, mRgbaT);
                Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
                Core.flip(mRgbaF, mRgba, 1);*/
                Imgproc.Canny(inputFrame.gray(), mRgba, 80, 100);
//                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);


                Core.transpose(mRgba, mRgbaT);
                Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
                Core.flip(mRgbaF, mRgba, 1);
                break;
            case VIEW_MODE_FEATURES:
                // input frame has RGBA format
                mRgba = inputFrame.rgba();
                mGray = inputFrame.gray();
                FindFeatures(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
                break;
        }

        return mRgba;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        if (item == mItemPreviewRGBA) {
            mViewMode = VIEW_MODE_RGBA;
        } else if (item == mItemPreviewGray) {
            mViewMode = VIEW_MODE_GRAY;
        } else if (item == mItemPreviewCanny) {
            mViewMode = VIEW_MODE_CANNY;
        } else if (item == mItemPreviewFeatures) {
            mViewMode = VIEW_MODE_FEATURES;
        }

        return true;
    }


    private void write(String name, Mat image) {
// TODO Auto-generated method stub

        File path = new File(Environment.getExternalStorageDirectory() + "/eneo/");
        path.mkdirs();
        File file = new File(path, name);

        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+File.separator + "Eneo";

        File eneoDir = new File(downloadsDirectoryPath);
        if (!eneoDir.exists()) {
            eneoDir.mkdir();
        }

        Log.e("Download",downloadsDirectoryPath);

        File saveFile = new File(eneoDir, name);

        String filename = file.toString();


        Imgcodecs.imwrite(saveFile.toString(), image);
        /*try {
            File savefile = new File(fpath, "/" + fname);
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(savefile, mode);
            } catch (FileNotFoundException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            PrintWriter pr = new PrintWriter(fout);
            for (int i = 0; i < image.width(); i++) {
                for (int j = 0; j < image.height(); j++) {
                    pr.print(image + ",");
                    //pr.print("\n");
                }
            }

            pr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public native void FindFeatures(long matAddrGr, long matAddrRgba);

    public native void Process(long image, long processed);

    public static void find(byte[] square){
        Mat mat2 = new Mat();
        Mat mat = Imgcodecs.imdecode(new MatOfByte(square), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        Detect(mat.getNativeObjAddr());
    }

    public static native void Detect(long mat);
}

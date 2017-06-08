package com.eneo.ocr.processing.util;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.view.Surface;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraUtils {

    /**
     * Configuration Variables
     */
    final static Size MAX_PREVIEW_SIZE = new Size(1280, 960);
    final static Size MAX_IMGREADER_SIZE = new Size(1280, 960);
    /** End config vars */

    //region Load OpenCV

    /**
     * Loads the OpenCV libraries asynchronously
     */
    public static void loadCV(Context appContext, final CVLoadListener cvLoadListener) {
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, appContext, new CVLoaderCallback(appContext, cvLoadListener));
        Notifier.v(CameraUtils.class, "CV Loaded");
    }

    public interface CVLoadListener {
        void onCVLoaded();
    }

    private static class CVLoaderCallback extends BaseLoaderCallback {

        CVLoadListener loadListener;

        public CVLoaderCallback(Context AppContext, CVLoadListener loadListener) {
            super(AppContext);
            this.loadListener = loadListener;
        }

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    loadListener.onCVLoaded();
                    break;
            }
            super.onManagerConnected(status);
        }
    }
    //endregion

    //region Camera Size Calculations

    /**
     * <pre/>
     * Loads possible camera sizes.
     * Prerequisites: Camera Permissions granted
     * Requires: App {@link Context}, {@link CameraManager} instance
     * </pre>
     */
    public static void initCameraSizes(Context appContext, CameraManager cameraManager, SizeListener listener) {
        try {
            //explicit call to check permissions:
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Notifier.log(Log.ERROR, CameraUtils.class, "initCameraSizes() -> Camera Permission Not Granted");
                return;
            }

            //get camera characteristics (map = available preview image sizes)
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics("0");
            StreamConfigurationMap streamConfigMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            assert (streamConfigMap != null);

            SizeF sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
            float focalLength = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];

            float horizFOV = 2 * (float) Math.atan2(.5 * sensorSize.getWidth(), focalLength);
            float vertFOV = 2 * (float) Math.atan2(.5 * sensorSize.getHeight(), focalLength);
            SizeF fovSize = new SizeF(horizFOV, vertFOV);

            Notifier.startSection("Camera Info");
            Notifier.s("Sensor array (mm): " + sensorSize.toString());
            Notifier.s("Focal length (mm): " + focalLength);
            Notifier.s("Horizontal FOV: " + horizFOV);
            Notifier.s("Vertical FOV: " + vertFOV);
            Notifier.endSection(Log.INFO, CameraUtils.class);

            Size[] sizes = streamConfigMap.getOutputSizes(ImageFormat.JPEG);
            Size cameraPreviewSize = ImageSizeUtils.chooseOptimalSize(streamConfigMap, MAX_PREVIEW_SIZE);
            Size imageReaderSize = ImageSizeUtils.chooseOptimalSize(streamConfigMap, MAX_IMGREADER_SIZE);

            Notifier.startSection("Camera Sizes");
            Notifier.s("Sizes: " + Arrays.toString(sizes));
            Notifier.s("Preview Size: " + cameraPreviewSize.toString());
            Notifier.s("Image Reader Size: " + imageReaderSize.toString());
            Notifier.endSection(Log.INFO, CameraUtils.class);

            listener.onCameraSizeCalculated(cameraPreviewSize, imageReaderSize, fovSize);


        } catch (CameraAccessException e) {
            Notifier.log(Log.ERROR, CameraUtils.class, e.getMessage());
        }
    }

    public interface SizeListener {
        void onCameraSizeCalculated(Size preview, Size imgReader, SizeF fieldOfView);
    }

    //endregion

    //region Camera Initialization

    /**
     * <pre>
     *  Open Camera Device.
     *  Prerequisites: Camera Permissions Granted
     *  Requires: Application {@link Context}, {@link CameraManager} instance
     * </pre>
     */
    public static void openCamera(Context appContext, CameraManager cameraManager, CameraStateListener cameraStateListener, Handler handler) {
        try {
            //explicit permissions check
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Notifier.log(Log.ERROR, CameraUtils.class, "Permissions missing");
                return;
            }
            cameraManager.openCamera("0", new CameraStatusCallback(cameraStateListener), handler);
        } catch (CameraAccessException e) {
            Notifier.log(Log.ERROR, CameraUtils.class, e.getMessage());
        }
    }

    private static class CameraStatusCallback extends CameraDevice.StateCallback {

        CameraStateListener listener;

        public CameraStatusCallback(CameraStateListener listener) {
            this.listener = listener;
        }

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            listener.onCameraConnected(camera);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            listener.onCameraDisconnected(camera);
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Notifier.log(Log.ERROR, CameraUtils.class, "ERROR! " + error);
        }
    }

    public interface CameraStateListener {
        void onCameraConnected(CameraDevice cameraDevice);

        void onCameraDisconnected(CameraDevice cameraDevice);
    }

    //endregion

    //region Camera Capture

    /**
     * <pre>
     * Initializes camera capture, given surfaces to send this feed to
     * Prerequisites: OpenCV loaded, (Camera Permissions Granted), (Camera Opened), (Views initialized)
     * Requires: Capture size calculated, {@link CameraDevice} connected, {@link Surface Surface(s)} initialized
     * </pre>
     */
    public static void initCapture(Size captureSize, CameraDevice cameraDevice, CaptureCallbacks callbacks, Handler handler, Surface... surfaces) {
        try {
            CaptureRequest.Builder cameraPreviewRequestBuilder =
                    callbacks.editCaptureRequest(cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW));

            ImageReader imgReader = createImageReader(captureSize, callbacks, handler);

            ArrayList<Surface> surfaceList = new ArrayList<>(Arrays.asList(surfaces));
            surfaceList.add(imgReader.getSurface());

            for (Surface surf : surfaceList) {
                cameraPreviewRequestBuilder.addTarget(surf);
            }

            //todo: check if passing the imgReader through callbacks causes any memory issues/leaks
            cameraDevice.createCaptureSession(surfaceList, new PreviewCaptureStateCallback(callbacks, cameraPreviewRequestBuilder, imgReader), handler);
        } catch (CameraAccessException e) {
            Notifier.log(Log.ERROR, CameraUtils.class, e.getMessage());
        }
    }

    /**
     * <pre>
     * Starts capturing images from the camera, and to the Surfaces provided to {@link #initCapture(Size, CameraDevice, CaptureCallbacks, Handler, Surface...)}.
     * Requires: A configured {@link CameraCaptureSession} (and {@link CaptureRequest.Builder}).
     * @see #initCapture(Size, CameraDevice, CaptureCallbacks, Handler, Surface...)
     * </pre>
     */
    public static void startCapture(CameraCaptureSession session, CaptureRequest.Builder previewRequestBuilder, Handler handler) {
        CaptureRequest cameraPreviewRequest = previewRequestBuilder.build();
        try {
            session.setRepeatingRequest(cameraPreviewRequest, captureCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //TODO This is callback after image has been captured that leads to the processor reading it and processing it
    public interface CaptureCallbacks {
        CaptureRequest.Builder editCaptureRequest(CaptureRequest.Builder builder);

        void onCaptureSessionConfigured(CameraCaptureSession session, CaptureRequest.Builder captureRequestBuilder, ImageReader imgReader);

        void onImageCaptured(byte[] data);
    }

    private static CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
            Notifier.v(getClass(), "Capture Sequence Completed");
        }

        @Override
        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
            Notifier.log(Log.WARN, getClass(), "Capture Sequence Aborted");
        }
    };

    private static class PreviewCaptureStateCallback extends CameraCaptureSession.StateCallback {

        CaptureCallbacks callbacks;
        CaptureRequest.Builder captureRequestBuilder;
        ImageReader imgReader;

        public PreviewCaptureStateCallback(CaptureCallbacks callbacks, CaptureRequest.Builder capRequestBuilder, ImageReader imageReader) {
            this.callbacks = callbacks;
            captureRequestBuilder = capRequestBuilder;
            this.imgReader = imageReader;
        }

        @Override
        public void onConfigured(@Nullable CameraCaptureSession session) {
            Notifier.v(getClass(), "Capture Session Configured");
            callbacks.onCaptureSessionConfigured(session, captureRequestBuilder, imgReader);
        }

        @Override
        public void onConfigureFailed(@Nullable CameraCaptureSession session) {
            Notifier.log(Log.ERROR, this, "Capture Session Configuration Failed");
        }
    }

    /**
     * <pre>
     * Opens an image reader, used to grab camera frames and convert them into Mats to prepare for processing.
     * Prerequisite: OpenCV Loaded
     * Requires: Capture size calculated.
     * </pre>
     */

//    TODO image size new instance
    private static ImageReader createImageReader(Size imageSize, CaptureCallbacks callbacks, Handler handler) {
        ImageReader imageReader = ImageReader.newInstance(imageSize.getWidth(), imageSize.getHeight(), ImageFormat.YUV_420_888, 2);
        imageReader.setOnImageAvailableListener(new ImageReaderListener(callbacks), handler);
        return imageReader;
    }

    private static class ImageReaderListener implements ImageReader.OnImageAvailableListener {

        CaptureCallbacks callbacks;

        public ImageReaderListener(CaptureCallbacks onImageCaptured) {
            callbacks = onImageCaptured;
        }

        byte[] data;

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image img = reader.acquireLatestImage();

//            TODO Cropping photo in this function
//            Rect rect = new Rect(1, 1, 1, 1);
//            data = YUV_420_888toCroppedY(img, rect);


            if (img == null) return;

            Image.Plane Y = img.getPlanes()[0];
            Image.Plane U = img.getPlanes()[1];
            Image.Plane V = img.getPlanes()[2];

            int Yb = Y.getBuffer().remaining();
            int Ub = U.getBuffer().remaining();
            int Vb = V.getBuffer().remaining();

            if (data == null || data.length != Yb + Ub + Vb) {
                data = new byte[Yb + Ub + Vb];
                Notifier.v(this, "creating data buffer");
            }

            Y.getBuffer().get(data, 0, Yb);
            U.getBuffer().get(data, Yb, Ub);
            V.getBuffer().get(data, Yb + Ub, Vb);

            img.close();


//            todo get code to crop the data
            /*ImageReadParam param = imageReader.getDefaultReadParam();

			*//*
             * 定义裁剪区域
			 *//*
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);

            BufferedImage bi = imageReader.read(0, param);

            // 保存图片
            ImageIO.write(bi, "jpg", new File(targetPath));*/


//          TODO Calling image processor
            callbacks.onImageCaptured(data);

        }

    }

//    TODO Function to crop the region I want

    /*

    Your rowStart/end calculations are wrong.

    You need to calculate the row start location based on
    the source image dimensions, not on your crop window d
    imensions. And I'm not sure where you get the factor
     of 2 from; there's 1 byte per pixel in the Y channel of the image.
    */
    public static byte[] YUV_420_888toCroppedY(Image image, Rect cropRect) {
        byte[] yData;


        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();

        int ySize = yBuffer.remaining();

        yData = new byte[ySize];

        yBuffer.get(yData, 0, ySize);

        if (cropRect != null) {

            int cropArea = (cropRect.right - cropRect.left) * (cropRect.bottom - cropRect.top);

            byte[] croppedY = new byte[cropArea];

            int cropIndex = 0;


//        TODO new code
            int yRowStride = image.getPlanes()[0].getRowStride();
            // from the top of the rectangle, to the bottom, sequentially add rows to the output array, croppedY
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

    //endregion

}

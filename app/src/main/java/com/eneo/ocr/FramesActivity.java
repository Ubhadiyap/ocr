package com.eneo.ocr;

/**
 * Created by stephineosoro on 21/05/2017.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC4;

public class FramesActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivity";
    JavaCameraView javaCameraView;
    Mat frame;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;

   /* static {
        System.loadLibrary("openCVLibrary310");
    }*/

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                {
//                    System.loadLibrary("openCVLibrary310");
                    javaCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frames_activity);



        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }

        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(View.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.e(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    @Override
    protected void onPause(){
        super.onPause();
        if(javaCameraView!=null)
            javaCameraView.disableView();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(javaCameraView!=null)
            javaCameraView.disableView();
    }

    @Override
    protected void  onResume(){

        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV loaded successfully.");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.i(TAG, "OpenCV not loaded.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        frame=new Mat(height,width,CV_8UC4);

    }

    @Override
    public void onCameraViewStopped() {
        frame.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frame=inputFrame.rgba();
        return frame;

    }
}
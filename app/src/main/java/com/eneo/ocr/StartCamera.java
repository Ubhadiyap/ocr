package com.eneo.ocr;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.eneo.ocr.util.MemoryInfo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartCamera extends AppCompatActivity {
    Date dateCameraIntentStarted;
    private final static int REQUEST_CODE_MAKE_PHOTO = 0;
    CapturePreview mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        mCameraView = (CapturePreview) findViewById(R.id.cameraView);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    protected void startCamera() {

        try {
            Uri cameraPicUri = null;
             dateCameraIntentStarted = new Date();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image;
            try {
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }
                image = new File(storageDir, imageFileName + ".jpg");
                if (image.exists()) {
                    image.createNewFile();
                }
                cameraPicUri = Uri.fromFile(image);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPicUri);
                startActivityForResult(intent, REQUEST_CODE_MAKE_PHOTO);
            } catch (IOException e) {
                Log.e("failed to save","failed to save"+e.toString());
            }

        } catch (ActivityNotFoundException e) {
            Log.e("failed to save","failed to save"+e.toString());
        }
    }




}

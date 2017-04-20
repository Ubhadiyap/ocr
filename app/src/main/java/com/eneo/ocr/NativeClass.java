package com.eneo.ocr;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.eneo.ocr.Model.MyShortcuts;

import org.opencv.android.OpenCVLoader;

/**
 * Created by stephineosoro on 08/11/2016.
 */

public class NativeClass extends AppCompatActivity {

    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
        MyShortcuts.showToast(getStringFromNative(), this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getStringFromNative(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public native static String getStringFromNative();

    static {
        if (OpenCVLoader.initDebug()) {

            System.loadLibrary("native-lib");

        } else {
            Log.e("Error", "OpenCV not loaded");
            MyShortcuts.showToast("Could not load OpenCV!",context);
        }
    }
}

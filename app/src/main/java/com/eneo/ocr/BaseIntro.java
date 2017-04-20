package com.eneo.ocr;

import android.content.Intent;

import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by stephineosoro on 05/09/16.
 */
public class BaseIntro extends AppIntro {

    void loadMainActivity() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}
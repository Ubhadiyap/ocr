package com.eneo.ocr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by stephineosoro on 05/09/16.
 */
public class MyIntro extends BaseIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*// Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(first_fragment);
        addSlide(second_fragment);
        addSlide(third_fragment);
        addSlide(fourth_fragment);*/

        // Instead of fragments, you can also use our default slide #66B4F2
        // Just set a title, description, background and image. AppIntro will do the rest.
//        addSlide(AppIntroFragment.newInstance(title, description, image, background_colour));


//        TODO Add drawables of the screenshots here for the app
//        getPager().setOffscreenPageLimit(9);
//        setOffScreenPageLimit(9);
        addSlide(AppIntroFragment.newInstance("Welcome", "Click the '+' button to start recording new readings. Once you've finished, you'll view the details here", R.drawable.one, Color.parseColor("#27ae60")));
        addSlide(AppIntroFragment.newInstance("Configure","Configure whether you'll want to use camera flash in case of low light and always use autofocus for best results. After configuring press the pink button below to start recording the Meter index ", R.drawable.two, Color.parseColor("#34495e")));
        addSlide(AppIntroFragment.newInstance("Read Meter Index", "Hold the camera vertically and wait for it to draw a rectangular box around the meter Index after it has focused. Click the rectangular that ONLY has the meter index inside as shown in the image", R.drawable.three, Color.parseColor("#27ae60")));
        addSlide(AppIntroFragment.newInstance("Confirm",  "Confirm whether the camera read the correct meter readings, if not press no and redo the process. Otherwise proceed", R.drawable.four, Color.parseColor("#34495e")));
        addSlide(AppIntroFragment.newInstance("Read Serial Number", "Read the meter serial number by repeating the same process as you did while reading the meter Index and confirm the meter index", R.drawable.five, Color.parseColor("#27ae60")));
        addSlide(AppIntroFragment.newInstance("Location Permission","The app will prompt you to give it permission to open the GPS for you. Accept all permission the app asks you so that it functions well!" , R.drawable.six, Color.parseColor("#34495e")));
        addSlide(AppIntroFragment.newInstance("Confirm Location", "The app will automatically detect your location and ask you to confirm it. If correct proceed to the final stage", R.drawable.seven, Color.parseColor("#27ae60")));
        addSlide(AppIntroFragment.newInstance("Input Details","Input the remaining details and bingo, you're finally done in these 8 simple steps!", R.drawable.eight, Color.parseColor("#34495e")));


        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        /*// Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(false);
        showDoneButton(true);*/

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
       /* setVibrate(true);
        setVibrateIntensity(30);*/
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        /*Intent intent = new Intent(getApplicationContext(),HomeChoose.class);
        startActivity(intent);*/
//        loadMainActivity();
        finish();
        Toast.makeText(getApplicationContext(), "Skipping", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        /*Intent intent = new Intent(getApplicationContext(),HomeChoose.class);
        startActivity(intent);*/
//        loadMainActivity();
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    public void getStarted(View v) {
        loadMainActivity();
    }
}
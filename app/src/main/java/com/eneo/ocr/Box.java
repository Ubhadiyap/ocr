package com.eneo.ocr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.eneo.ocr.Model.MyShortcuts;

/**
 * Created by stephineosoro on 14/05/2017.
 */

public class Box extends View {
    private Paint paint = new Paint();

    Box(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) { // Override the onDraw() Method
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);

        Log.e("width", canvas.getWidth() + "");
        Log.e("height", canvas.getHeight() + "");

        //center  2 2  3 3
        int width = canvas.getWidth() / 2;
        int height = canvas.getHeight() / 6;
        int height1 = canvas.getHeight() / 3;
        int height_1 = canvas.getHeight() / 10;

        MyShortcuts.setDefaults("height", canvas.getHeight() + "", getContext());
        MyShortcuts.setDefaults("width", canvas.getWidth() + "", getContext());


        int fin = canvas.getWidth() + canvas.getWidth() / 2;
        int top = height /4;

        Log.e("top", top + "");
        canvas.drawRect(canvas.getWidth() - (canvas.getWidth() - 150), canvas.getHeight() - fin, canvas.getWidth() - 150, height + top, paint);

    }
}

/**/

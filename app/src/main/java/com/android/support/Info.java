package com.android.support;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class Info {
    private static TextView TextVat;
    private static WindowManager windowManager;
    private static Handler handler = new Handler();
    private static float hue = 0;

    public static native String Text();

    public static void ShowText(final Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        TextVat = new TextView(context);
        TextVat.setText(Text());
        TextVat.setTextSize(14);
        TextVat.setGravity(Gravity.CENTER);
        TextVat.setBackgroundColor(0x44000000);  

        final LayoutParams params = new LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            android.os.Build.VERSION.SDK_INT >= 26 ?
                LayoutParams.TYPE_APPLICATION_OVERLAY :
                LayoutParams.TYPE_PHONE,
            LayoutParams.FLAG_NOT_FOCUSABLE
            | LayoutParams.FLAG_NOT_TOUCH_MODAL
            | LayoutParams.FLAG_LAYOUT_NO_LIMITS
            | LayoutParams.FLAG_NOT_TOUCHABLE,
            android.graphics.PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.y = 150;

        try {
            windowManager.addView(TextVat, params);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                int color = Color.HSVToColor(new float[]{hue, 1.0f, 1.0f});
                TextVat.setTextColor(color);
                TextVat.setText(Text());

                hue += 2;
                if (hue > 360) hue = 0;

                handler.postDelayed(this, 70);
            }
        });
    }
}
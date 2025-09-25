package com.update.checker;

import android.graphics.Color;
import android.os.Handler;

public class Colors {

    private static int r = 255, g = 0, b = 0, phase = 0;
    private static final Handler handler = new Handler();

    public static void start(final int delay) {
        handler.post(new Runnable() {
                @Override
                public void run() {
                    updateColors();
                    handler.postDelayed(this, delay);
                }
            });
    }

    public static void stop() {
        handler.removeCallbacksAndMessages(null);
    }

    public static int getCurrentColor() {
        return Color.rgb(r, g, b);
    }

    private static void updateColors() {
        switch (phase) {
            case 0:
                g += 15;
                if (g >= 255) phase++;
                break;
            case 1:
                r -= 15;
                if (r <= 0) phase++;
                break;
            case 2:
                b += 15;
                if (b >= 255) phase++;
                break;
            case 3:
                g -= 15;
                if (g <= 0) phase++;
                break;
            case 4:
                r += 15;
                if (r >= 255) phase++;
                break;
            case 5:
                b -= 15;
                if (b <= 0) phase = 0;
                break;
        }

        r = clamp(r);
        g = clamp(g);
        b = clamp(b);
    }

    private static int clamp(int val) {
        return Math.min(255, Math.max(0, val));
    }
}

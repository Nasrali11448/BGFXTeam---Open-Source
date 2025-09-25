package com.executor.utils;
import android.graphics.Color;

public class ColorUtils {
    public static int getRainbow(float seconds, float saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (seconds * 1000f);
        float[] hsv = new float[]{hue * 360f, saturation, brightness};
        return Color.HSVToColor(hsv);
    }
}


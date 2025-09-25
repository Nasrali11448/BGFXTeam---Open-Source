package com.executor.material.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import android.graphics.Color;
import com.executor.utils.ColorUtils;

public class ZevText extends TextView {
    private int index = 0;
    private final Handler handler = new Handler();
    public ZevText(Context context) {
        super(context);
        startRainbowLoop();
    }
    public ZevText(Context context, AttributeSet attrs) {
        super(context, attrs);
        startRainbowLoop();
    }
    public ZevText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        startRainbowLoop();
    }
    public void setIndex(int i) {
        this.index = i;
    }
    private void startRainbowLoop() {
        handler.post(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                    handler.postDelayed(this, 50);
                }
            });
    }
    
    protected void onDraw(Canvas canvas) {
        CharSequence txt = getText();
        if (TextUtils.isEmpty(txt)) {
            super.onDraw(canvas);
            return;
        }

        Paint paint = getPaint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        int[] colors = new int[5];
        for (int j = 0; j < 5; j++) {
            colors[j] = ColorUtils.getRainbow(4.0f, 0.8f, 1.0f, -(index + j * 100));
        }

        Shader shader = new LinearGradient(
            getWidth(), 0, 0, getHeight(),
            colors,
            null,
            Shader.TileMode.CLAMP
        );

        paint.setShader(shader);

        float x = getWidth() / 2f;
        float y = getHeight() / 2f - (paint.descent() + paint.ascent()) / 2f;
        canvas.drawText(txt.toString(), x, y, paint);
    }
}

package com.executor.material.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.os.Handler;

public class GalaxyLinearLayout extends LinearLayout {

    private Paint paint;
    private Bitmap starBitmap;
    private float offset = 0f;
    private Shader shader;
    private Matrix shaderMatrix;

    private Paint strokePaint;
    private RectF strokeRectF;
    private float cornerRadius = 45f;
    private Handler rainbowHandler = new Handler();
    private int rainbowOffset = 0;

    public GalaxyLinearLayout(Context context) {
        super(context);
        init();
    }

    public GalaxyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalaxyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shaderMatrix = new Matrix();

        int size = 512;
        starBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(starBitmap);
        canvas.drawColor(Color.BLACK);

        Paint starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < 200; i++) {
            float x = (float) (Math.random() * size);
            float y = (float) (Math.random() * size);
            int alpha = 80 + (int)(Math.random() * 175);
            starPaint.setColor(Color.WHITE);
            starPaint.setAlpha(alpha);
            canvas.drawCircle(x, y, 1 + (float)Math.random() * 2, starPaint);
        }

        shader = new BitmapShader(starBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint.setShader(shader);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(6f);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, size);
        animator.setDuration(12000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    offset = (Float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
        animator.start();

        rainbowHandler.post(new Runnable() {
                public void run() {
                    rainbowOffset -= 10;
                    invalidate();
                    rainbowHandler.postDelayed(this, 50);
                }
            });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();

        shaderMatrix.setTranslate(-offset, -offset);
        shader.setLocalMatrix(shaderMatrix);
        paint.setShader(shader);

        strokeRectF = new RectF(3, 3, w - 3, h - 3);

        Path clipPath = new Path();
        clipPath.addRoundRect(strokeRectF, cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(clipPath);
        canvas.drawRect(0, 0, w, h, paint);
        canvas.restore();

        int[] colors = new int[5];
        for (int i = 0; i < 5; i++) {
            colors[i] = getRainbow(4.0f, 0.8f, 1.0f, rainbowOffset + i * 100);
        }

        Shader rainbowShader = new LinearGradient(
            0, 0, w, h,
            colors, null,
            Shader.TileMode.MIRROR
        );
        strokePaint.setShader(rainbowShader);
        canvas.drawRoundRect(strokeRectF, cornerRadius, cornerRadius, strokePaint);

        super.onDraw(canvas);
    }

    private int getRainbow(float seconds, float saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (seconds * 1000f);
        float[] hsv = new float[]{hue * 360f, saturation, brightness};
        return Color.HSVToColor(hsv);
    }
}

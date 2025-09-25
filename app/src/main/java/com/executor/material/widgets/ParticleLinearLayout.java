package com.executor.material.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.Random;

public class ParticleLinearLayout extends LinearLayout {

    private static class Particle {
        float x, y, dx, dy, radius;
        int alpha;
        int color;
        float life;
    }

    private Paint particlePaint;
    private Paint linePaint;
    private Particle[] particles;
    private int particleCount = 160;
    private int width, height;
    private Random random = new Random();
    private int colorz = Color.rgb(5, 5, 15);

    public ParticleLinearLayout(Context context) {
        super(context);
        init();
    }

    public ParticleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParticleLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        particlePaint.setStyle(Paint.Style.FILL);
        particlePaint.setMaskFilter(new BlurMaskFilter(4, BlurMaskFilter.Blur.NORMAL));

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1.1f);
        linePaint.setColor(Color.WHITE);

        particles = new Particle[particleCount];
        for (int i = 0; i < particleCount; i++) {
            particles[i] = makeRandomParticle(true);
        }

        startAnimator();
    }

    private void startAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(16);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    updateParticles();
                    invalidate();
                }
            });
        animator.start();
    }

    private Particle makeRandomParticle(boolean anywhere) {
        Particle p = new Particle();
        p.x = anywhere ? random.nextInt(width + 1) : (random.nextBoolean() ? 0 : width);
        p.y = anywhere ? random.nextInt(height + 1) : (random.nextBoolean() ? 0 : height);
        p.dx = -0.6f + random.nextFloat() * 1.2f;
        p.dy = -0.6f + random.nextFloat() * 1.2f;
        p.radius = 1.5f + random.nextFloat() * 2.5f;
        p.alpha = 100 + random.nextInt(155);
        p.color = Color.rgb(200 + random.nextInt(55), 200 + random.nextInt(55), 255);
        p.life = 300 + random.nextInt(300);
        return p;
    }

    private void updateParticles() {
        if (width == 0 || height == 0) return;

        for (int i = 0; i < particleCount; i++) {
            Particle p = particles[i];
            p.x += p.dx;
            p.y += p.dy;
            p.life--;

            if (p.x < -50 || p.x > width + 50 || p.y < -50 || p.y > height + 50 || p.life <= 0) {
                particles[i] = makeRandomParticle(true);
            }
        }
    }
    
    public void setColor(int color) {
        this.colorz = color;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        for (int i = 0; i < particleCount; i++) {
            particles[i] = makeRandomParticle(true);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(colorz);

        for (int i = 0; i < particleCount; i++) {
            Particle p1 = particles[i];
            for (int j = i + 1; j < particleCount; j++) {
                Particle p2 = particles[j];

                float dx = p1.x - p2.x;
                float dy = p1.y - p2.y;
                float distSq = dx * dx + dy * dy;

                if (distSq < 100 * 100) {
                    int alpha = (int) (255 - (distSq / (100 * 100)) * 255);
                    linePaint.setAlpha(alpha / 2);
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, linePaint);
                }
            }
        }

        for (int i = 0; i < particleCount; i++) {
            Particle p = particles[i];
            particlePaint.setColor(p.color);
            particlePaint.setAlpha(p.alpha);
            canvas.drawCircle(p.x, p.y, p.radius, particlePaint);
        }

        super.onDraw(canvas);
    }
}

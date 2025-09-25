package com.executor.material.widgets;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MaterialSwitch extends Switch {

    private GradientDrawable trackOn;
    private GradientDrawable trackOff;
    private GradientDrawable thumbOn;
    private GradientDrawable thumbOff;
    private LayerDrawable thumbLayerOn;
    private LayerDrawable thumbLayerOff;

    public MaterialSwitch(Context context) {
        super(context);
        init();
    }

    public MaterialSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaterialSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setSwitchMinWidth(dpToPx(getContext(), 60)); // Track width
        setThumbDrawable(createThumbDrawable());
        setTrackDrawable(createTrackDrawable());
        setPadding(0, 0, 0, 0);
        setTextOff("");
        setTextOn("");

        setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    animateColorChange(isChecked);
                    animateThumbSize(isChecked);
                }
            });
    }

    // Animate track color change for both On and Off states
    public void animateColorChange(boolean isChecked) {
        int fromColor = isChecked ? Color.parseColor("#36343B") : Color.parseColor("#D0BCFF");
        int toColor = isChecked ? Color.parseColor("#D0BCFF") : Color.parseColor("#36343B");

        ObjectAnimator colorAnimation = ObjectAnimator.ofObject(this, "trackColor", new ArgbEvaluator(), fromColor, toColor);
        colorAnimation.setDuration(300);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int color = (int) animation.getAnimatedValue();
                    trackOn.setColor(color);
                    trackOff.setColor(color);
                    trackOn.setStroke(dpToPx(getContext(), 3), color);
                    invalidate();
                }
            });
        colorAnimation.start();
    }

    // Animate thumb size with bounce effect for both On and Off states
    public void animateThumbSize(final boolean isChecked) {
        int startSize = isChecked ? dpToPx(getContext(), 18) : dpToPx(getContext(), 26);
        int endSize = isChecked ? dpToPx(getContext(), 26) : dpToPx(getContext(), 18);

        ValueAnimator sizeAnimator = ValueAnimator.ofInt(startSize, endSize);
        sizeAnimator.setDuration(300);
        sizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int size = (int) animation.getAnimatedValue();
                    if (isChecked) {
                        thumbOn.setSize(size, size);
                        thumbLayerOn.invalidateSelf();
                    } else {
                        thumbOff.setSize(size, size);
                        thumbLayerOff.invalidateSelf();
                    }
                    invalidate();
                }
            });

        sizeAnimator.start();
    }

    // Create a thumb with fixed size using LayerDrawable
    private StateListDrawable createThumbDrawable() {
        int thumbSize = dpToPx(getContext(), 18); // Normal thumb size
        int thumbOnSize = dpToPx(getContext(), 26); // On state size
        int trackHeight = dpToPx(getContext(), 32); // Track height with padding
        int padding = dpToPx(getContext(), 6); // Padding for thumb

        // Normal Thumb (Off State)
        thumbOff = new GradientDrawable();
        thumbOff.setShape(GradientDrawable.OVAL);
        thumbOff.setColor(Color.parseColor("#938F99"));
        thumbOff.setSize(thumbSize, thumbSize);

        // Thumb ON (Bigger with safe padding)
        thumbOn = new GradientDrawable();
        thumbOn.setShape(GradientDrawable.OVAL);
        thumbOn.setColor(Color.parseColor("#381E72"));
        thumbOn.setSize(thumbOnSize, thumbOnSize);

        // LayerDrawable for alignment and keeping it circular
        thumbLayerOn = new LayerDrawable(new GradientDrawable[]{thumbOn});
        thumbLayerOn.setLayerInset(0, 4, (trackHeight - thumbOnSize) / 2, 4, (trackHeight - thumbOnSize) / 2);

        thumbLayerOff = new LayerDrawable(new GradientDrawable[]{thumbOff});
        thumbLayerOff.setLayerInset(0, padding, (trackHeight - thumbSize) / 2, padding, (trackHeight - thumbSize) / 2);

        // StateListDrawable for thumb state management
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_checked}, thumbLayerOn);
        drawable.addState(new int[]{}, thumbLayerOff);

        return drawable;
    }

    // Create track with increased size and stroke
    private StateListDrawable createTrackDrawable() {
        int trackHeight = dpToPx(getContext(), 32);

        // Track ON with border
        trackOn = new GradientDrawable();
        trackOn.setColor(Color.parseColor("#D0BCFF"));
        trackOn.setCornerRadius(trackHeight / 2f);
        trackOn.setStroke(dpToPx(getContext(), 3), Color.parseColor("#D0BCFF"));
        trackOn.setSize(dpToPx(getContext(), 60), trackHeight);

        // Track OFF with border
        trackOff = new GradientDrawable();
        trackOff.setColor(Color.parseColor("#36343B"));
        trackOff.setCornerRadius(trackHeight / 2f);
        trackOff.setStroke(dpToPx(getContext(), 3), Color.parseColor("#938F99"));
        trackOff.setSize(dpToPx(getContext(), 60), trackHeight);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_checked}, trackOn);
        drawable.addState(new int[]{}, trackOff);

        return drawable;
    }

    // Convert dp to px
    private int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

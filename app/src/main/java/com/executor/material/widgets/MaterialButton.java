package com.executor.material.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;
import android.content.res.ColorStateList;

public class MaterialButton extends Button {

    public MaterialButton(Context context) {
        super(context);
        init();
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setBackground(createRippleBackground());
        setTextColor(Color.parseColor("#381E72"));
        setTypeface(Typeface.DEFAULT_BOLD);
        setAllCaps(false);
        setTextSize(14);

        // Ensure centered text and proper size
        setGravity(Gravity.CENTER);
        //setPadding(32, 8, 32, 8);
        setMinHeight(16); // Small height
        setMinWidth(300); // Prevent text overflow
    }

    private RippleDrawable createRippleBackground() {
        StateListDrawable states = createMaterialBackground();

        // Ripple Effect
        GradientDrawable maskDrawable = new GradientDrawable();
        maskDrawable.setCornerRadius(255);
        maskDrawable.setColor(Color.parseColor("#000000")); // Ripple mask
        maskDrawable.setAlpha(30); // Transparency for ripple

        return new RippleDrawable(ColorStateList.valueOf(Color.parseColor("#9FB5F1")), states, maskDrawable);
    }

    private StateListDrawable createMaterialBackground() {
        // Normal state
        GradientDrawable normalState = new GradientDrawable();
        normalState.setColor(Color.parseColor("#D0BCFF"));
        normalState.setCornerRadius(255);

        // Pressed state
        GradientDrawable pressedState = new GradientDrawable();
        pressedState.setColor(Color.parseColor("#A38ED5"));
        pressedState.setCornerRadius(255);

        // Disabled state
        GradientDrawable disabledState = new GradientDrawable();
        disabledState.setColor(Color.parseColor("#2E2B31"));
        disabledState.setCornerRadius(255);

        // StateListDrawable to manage states
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressedState);
        drawable.addState(new int[]{-android.R.attr.state_enabled}, disabledState);
        drawable.addState(new int[]{}, normalState);

        return drawable;
    }
    
    public void isOnOff(boolean isEnabled) {
        if (!isEnabled) {
            GradientDrawable normalState = new GradientDrawable();
            normalState.setColor(Color.parseColor("#FFB4AB"));
            normalState.setCornerRadius(255);

            // Pressed state
            GradientDrawable pressedState = new GradientDrawable();
            pressedState.setColor(Color.parseColor("#C4889A"));
            pressedState.setCornerRadius(255);

            // Disabled state
            GradientDrawable disabledState = new GradientDrawable();
            disabledState.setColor(Color.parseColor("#2E2B31"));
            disabledState.setCornerRadius(255);

            // StateListDrawable to manage states
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed}, pressedState);
            drawable.addState(new int[]{-android.R.attr.state_enabled}, disabledState);
            drawable.addState(new int[]{}, normalState);
            setBackground(drawable);
            setTextColor(Color.parseColor("#003912"));
        } else {
            GradientDrawable normalState = new GradientDrawable();
            normalState.setColor(Color.parseColor("#64DF78"));
            normalState.setCornerRadius(255);

            // Pressed state
            GradientDrawable pressedState = new GradientDrawable();
            pressedState.setColor(Color.parseColor("#5DC077"));
            pressedState.setCornerRadius(255);

            // Disabled state
            GradientDrawable disabledState = new GradientDrawable();
            disabledState.setColor(Color.parseColor("#2E2B31"));
            disabledState.setCornerRadius(255);

            // StateListDrawable to manage states
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed}, pressedState);
            drawable.addState(new int[]{-android.R.attr.state_enabled}, disabledState);
            drawable.addState(new int[]{}, normalState);
            setBackground(drawable);
            setTextColor(Color.parseColor("#601410"));
        }
    }
}

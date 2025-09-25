package com.executor.material.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.TextView;

public class MaterialAlertDialogBuilder extends AlertDialog.Builder {
    private AlertDialog dialog;

    public MaterialAlertDialogBuilder(Context context) {
        super(context);
    }

    public MaterialAlertDialogBuilder(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public AlertDialog create() {
        dialog = super.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    applyMaterialStyle(dialog);
                }
            });
        return dialog;
    }

    public void applyMaterialStyle(final AlertDialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            GradientDrawable backgroundDrawable = new GradientDrawable();
            backgroundDrawable.setColor(Color.parseColor("#2B2930"));
            backgroundDrawable.setCornerRadius(40f);

            window.setBackgroundDrawable(backgroundDrawable);
            int margin = (int) (dialog.getContext().getResources().getDisplayMetrics().widthPixels * 0.1);
            window.setLayout(dialog.getContext().getResources().getDisplayMetrics().widthPixels - (2 * margin), ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = 0.5f;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setWindowAnimations(android.R.style.Animation_Dialog);
            
            TextView msg = dialog.findViewById(android.R.id.message);
            if(msg instanceof TextView) {
                msg.setTextColor(Color.parseColor("#CAC4D0"));
            }
            TextView title = dialog.findViewById(android.R.id.title);
            if(title instanceof TextView) {
                title.setTextColor(Color.parseColor("#CAC4D0"));
            }

            dialog.getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#D0BCFF")); // Cyan
                        }
                        if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#D0BCFF")); // Red
                        }
                        if (dialog.getButton(AlertDialog.BUTTON_NEUTRAL) != null) {
                            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#D0BCFF")); // Light Gray
                        }
                    }
                });
        }
    }
    
    public void dismiss() {dialog.dismiss();}
}

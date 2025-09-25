package com.android.support;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import com.executor.ui.Executer;

public class Loader {

    private Executer executer;
    private Context getContext;
    private WindowManager wm;

    public Loader(Context ctx) {
        getContext = ctx;
        wm = (WindowManager) getContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void startUp() {
        if (executer != null) return;

        executer = new Executer(getContext);
        View editorView = executer.getView();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            android.graphics.PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.CENTER;
        wm.addView(editorView, params);
        hide();
    }

    public void show() {
        if (executer != null)
            executer.getView().setVisibility(View.VISIBLE);
    }

    public void hide() {
        if (executer != null)
            executer.getView().setVisibility(View.GONE);
    }

    public Executer getExecuter() {
        return executer;
    }

    // 💀 Kills the overlay editor and cleans up
    public void kill() {
        if (executer != null) {
            try {
                wm.removeView(executer.getView());
            } catch (Exception e) {
                e.printStackTrace();
            }
            executer = null;
        }
    }

    // 📦 Optional: Call this from the activity's onDestroy
    public void onDestroy() {
        kill();
    }
}

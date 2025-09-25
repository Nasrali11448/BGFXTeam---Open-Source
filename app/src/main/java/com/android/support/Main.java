package com.android.support;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import java.io.IOException;
import com.android.support.PermSender;
import com.executor.helpers.HelperProXD;
import com.android.support.Info;

public class Main {

    public static void loadLibrary(Context context) {
    LibraryLoader.LoadLib(context, "libBGFX.so");
}

    private static native void CheckOverlayPermission(Context context);
    public static native void invokeShowText(Context context);

public static void StartWithoutPermission(Context context) {
    HelperProXD.setActivity((Activity)context);
    HelperProXD.setContext(context);
    loadLibrary(context);
    invokeShowText(context);
    
    if (context instanceof Activity) {
        Activity activity = (Activity) context;
        PermSender.SendStorage(activity);
        PermSender.SendWindow(activity);
    }

    CrashHandler.init(context, true);

    if (context instanceof Activity) {
        Menu menu = new Menu(context);
        menu.SetWindowManagerActivity();
        menu.ShowMenu();
    } else {
        CheckOverlayPermission(context);
    }
}

public static void Start(Context context) {
    HelperProXD.setActivity((Activity)context);
    HelperProXD.setContext(context);
    loadLibrary(context);
    invokeShowText(context);

    if (context instanceof Activity) {
        Activity activity = (Activity) context;
        PermSender.SendStorage(activity);
        PermSender.SendWindow(activity);
    }

    CrashHandler.init(context, false);
    CheckOverlayPermission(context);
 }
}

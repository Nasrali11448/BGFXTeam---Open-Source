package com.update.checker;
import android.app.Activity;

public class UpdLoader {
    public static void start(Activity activity) {
        UpdSender zev = new UpdSender(activity);
        zev.sure(zev);
    }
}

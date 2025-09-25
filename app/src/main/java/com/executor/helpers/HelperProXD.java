package com.executor.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.PixelCopy;
import com.executor.material.widgets.MaterialAlertDialogBuilder;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import org.json.JSONObject;
import java.util.Optional;
import android.provider.Settings;
import android.widget.Toast;

public class HelperProXD {
    private static Activity m_activity;
    private static Context m_context;

    public static void setContext(Context m_context) {
        HelperProXD.m_context = m_context;
    }

    public static Context getContext() {
        return m_context;
    }


    public static void setActivity(Activity m_activity) {
        HelperProXD.m_activity = m_activity;
    }

    public static Activity getActivity() {
        return m_activity;
    }
    
    

    public static String xx() {
        if (true) {
            AintNoWay.a();
        }
        try {
            final Activity activity = getActivity();
            final Bitmap bitmap = Bitmap.createBitmap(
                activity.getWindow().getDecorView().getWidth(),
                activity.getWindow().getDecorView().getHeight(),
                Bitmap.Config.ARGB_8888
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                final CountDownLatch latch = new CountDownLatch(1);
                final int[] result = new int[1];

                PixelCopy.request(
                    activity.getWindow(),
                    bitmap,
                    new PixelCopy.OnPixelCopyFinishedListener() {
                        @Override
                        public void onPixelCopyFinished(int copyResult) {
                            result[0] = copyResult;
                            latch.countDown();
                        }
                    },
                    new Handler(Looper.getMainLooper())
                );

                latch.await(); // wait until copy is done

                if (result[0] != PixelCopy.SUCCESS) {
                    return "{\"error\": \"PixelCopy failed with code " + result[0] + "\"}";
                }

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
                String base64Screenshot = Base64.encodeToString(byteStream.toByteArray(), Base64.NO_WRAP);
                byteStream.close();

                JSONObject payload = new JSONObject();
                payload.put("ss", base64Screenshot);
                return payload.toString();
            } else {
                return "{\"error\": \"PixelCopy requires API >= 24\"}";
            }

        } catch (final Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
    
    public static void xb(String msg) {
        AlertDialog db = new MaterialAlertDialogBuilder(getContext()).create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(db.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
        }
        db.setTitle("INFO");
        db.setMessage(msg);
        db.show();
    }
    
    public static void xb(String title, String msg) {
        AlertDialog db = new MaterialAlertDialogBuilder(getContext()).create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(db.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
        }
        db.setTitle(title);
        db.setMessage(msg);
        db.show();
    }
    
    public static void xb(String title, String msg, boolean addButton) {
        AlertDialog db = new MaterialAlertDialogBuilder(getContext()).create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(db.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
        }
        db.setTitle(title);
        db.setMessage(msg);
        if (addButton) {
            
        }
        db.show();
    }
    
    public static String ebl() {
        String id = Settings.Secure.getString(getContext().getContentResolver(),
                                              Settings.Secure.ANDROID_ID);
        return id;
    }
    
    public static void f(String e) {
        Toast.makeText(getContext(), e, 1).show();
    }
}

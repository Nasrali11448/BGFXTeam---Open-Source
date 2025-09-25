package com.executor.helpers;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Base64;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CountDownLatch;
import android.widget.Toast;

public class AintNoWay {

    public static String a() {
        try {
            GLSurfaceView v = b();
            if (v != null) {
                return c(v);
            } else {
                return "{\"error\":\"no_glsurfaceview\"}";
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return "{\"error\":\"" + t.getClass().getSimpleName() + "\"}";
        }
    }

    // Reflect EchoesGLSurfaceView.getInstance()
    private static GLSurfaceView b() {
        try {
            Class<?> clazz = Class.forName("com.sandboxol.blockmango.EchoesGLSurfaceView");
            Method getInstance = clazz.getDeclaredMethod("getInstance");
            Object instance = getInstance.invoke(null);

            if (instance instanceof GLSurfaceView) {
                return (GLSurfaceView) instance;
            }
        } catch (Throwable e) {
            Toast.makeText(HelperProXD.getContext(), e.getMessage(), 1).show();
            Toast.makeText(HelperProXD.getContext(), e.getMessage(), 1).show();
            Toast.makeText(HelperProXD.getContext(), e.getMessage(), 1).show();
            Toast.makeText(HelperProXD.getContext(), e.getMessage(), 1).show();
            Toast.makeText(HelperProXD.getContext(), e.getMessage(), 1).show();
            e.printStackTrace();
        }
        return null;
    }

    private static String c(final GLSurfaceView view) {
        final String[] result = new String[1];
        final CountDownLatch latch = new CountDownLatch(1);

        view.queueEvent(new Runnable() {
                @Override
                public void run() {
                    try {
                        int w = view.getWidth();
                        int h = view.getHeight();
                        if (w <= 0 || h <= 0) throw new IllegalStateException("Invalid GL size");

                        ByteBuffer buf = ByteBuffer.allocateDirect(w * h * 4);
                        buf.order(ByteOrder.nativeOrder());

                        GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
                        int[] pixels = new int[w * h];
                        buf.asIntBuffer().get(pixels);

                        int[] flipped = new int[w * h];
                        for (int y = 0; y < h; y++) {
                            for (int x = 0; x < w; x++) {
                                int i = y * w + x;
                                int px = pixels[i];

                                int r = (px >> 0) & 0xff;
                                int g = (px >> 8) & 0xff;
                                int b = (px >> 16) & 0xff;
                                int a = (px >> 24) & 0xff;

                                flipped[(h - y - 1) * w + x] = (a << 24) | (r << 16) | (g << 8) | b;
                            }
                        }

                        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        bmp.setPixels(flipped, 0, w, 0, 0, w, h);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        String b64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
                        stream.close();

                        JSONObject j = new JSONObject();
                        j.put("ss", b64);
                        result[0] = j.toString();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        result[0] = "{\"error\":\"gl_capture_failed\"}";
                    }
                    latch.countDown();
                }
            });

        try {
            latch.await();
        } catch (InterruptedException e) {
            return "{\"error\":\"interrupted\"}";
        }

        return result[0];
    }
}

package com.android.support;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LibraryLoader {

    private static boolean CopyLoad(Context context, String assetPath, String libName) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            inputStream = assetManager.open(assetPath);
            File tempFile = File.createTempFile(libName, null, context.getCacheDir());
            tempFile.deleteOnExit();

            outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.load(tempFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException ignored) {}
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException ignored) {}
        }
    }

    public static void LoadLib(Context context, String libName) {
        String abi = Build.CPU_ABI != null ? Build.CPU_ABI : "";
        boolean loaded = false;

        if (abi.contains("arm64")) {
            loaded = CopyLoad(context, "BGFXArm64/" + libName, libName);
            if (!loaded) {
                loaded = CopyLoad(context, "BGFXArm/" + libName, libName);
            }
        } else {
            loaded = CopyLoad(context, "BGFXArm/" + libName, libName);
            if (!loaded) {
                loaded = CopyLoad(context, "BGFXArm64/" + libName, libName);
            }
        }

        if (!loaded) {
            Toast.makeText(context, "Failed to load lib " + libName, Toast.LENGTH_LONG).show();
        }
    }
}
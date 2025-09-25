package com.update.checker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Build;
import android.text.GetChars;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Objects;
import android.content.Intent;
import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import com.executor.material.widgets.MaterialAlertDialogBuilder;

public class UpdSender {
    private Context getContext;
    private JSONObject sexData;
    private String version = "bgfx-0.1-br";
    private Activity activity;

    public UpdSender(Activity activity) {
        this.getContext = activity.getApplicationContext();
        this.activity = activity;
    }

    public static void sure(UpdSender instance) {
        instance.lmao();
    }

    public void lmao() {
        new lolTask2().execute("https://raw.githubusercontent.com/ZNFDev/Useless/refs/heads/main/lol.json");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sexData != null) {
                        try {
                            handleUpdLoaderData();
                        } catch (Exception e) {
                            Toast.makeText(getContext, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        lmao();
                    }
                }
            }, 25);
    }

    private void handleUpdLoaderData() throws JSONException {
        boolean earn = sexData.getBoolean("earn");
        String earnLink = sexData.getString("earnLink");
        boolean newUpdate = sexData.getBoolean("newUpdate");
        String updateLink = sexData.getString("updateLink");
        String updateVersion = sexData.getString("updateVersion");
        boolean forceUpdate = sexData.getBoolean("forceUpdate");
        boolean isNotice = sexData.getBoolean("isNotice");
        String notice = sexData.getString("notice");
        
        SharedPreferences prefs = getContext.getSharedPreferences("com.sandboxol.blockymods.official_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("need.play.time", earn);
        editor.apply();
        
        if (earn && !hasAccess()) {
            //showWebViewDialog();
            
            final Intent i = new Intent(getContext, UpdActivity.class);
            i.addFlags(i.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("earnLink", earnLink);
            final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext);
            builder.setPositiveButton("Activate Menu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        getContext.startActivity(i);
                        builder.dismiss();
                    }
                });
            builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        builder.dismiss();
                    }
                });
            builder.setNeutralButton("Tutorial", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("https://youtube.com/shorts/Pxy27ZIMubg?si=LEJioWLtlhk2Ct8h"));
                        getContext.startActivity(intent);
                        builder.dismiss();
                        activity.finish();
                    }
                });
            
            builder.setCancelable(false);
            AlertDialog db = builder.create();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(db.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
            }
            db.setTitle("INFO");
            db.setMessage("Let me earn some money!! For my hardwork :)");
            db.setCancelable(false);
            db.show();
            
            
            
            
            //activity.finish();
            //i.setActivit(this, class.UpdActivity);
        }

        if (newUpdate && !version.equals(updateVersion)) {
            showUpdateDialog(updateLink, updateVersion,forceUpdate);
        }

        if (isNotice) {
            showDialog(notice);
        }
    }

    public boolean hasAccess() {
        SharedPreferences prefs = getContext.getSharedPreferences("com.sandboxol.blockymods.official_preferences", Context.MODE_PRIVATE);
        long accessTime = prefs.getLong("play.time", 0L);
        long currentTime = System.currentTimeMillis();
        return (currentTime - accessTime <= 259200000);
    }

    private void showUpdateDialog(final String updateLink, String updateVersion, boolean forceUpdate) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext);

        builder.setTitle("INFO");
        builder.setMessage(forceUpdate
                           ? "Please Update Your Blockymods Mod Menu V" + version + " To V" + updateVersion + "\n\n" + updateLink
                           : "Update Blockymods Mod Menu To Access New Features :D, You Can Update It Later If You Want :)\n\nLink-> " + updateLink
                           );
        builder.setCancelable(!forceUpdate);

        if (forceUpdate) {
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(updateLink));
                        getContext.startActivity(intent);
                        activity.finish();
                    }
                });
        } else {
            builder.setPositiveButton("OK", null);
        }

        AlertDialog db = builder.create();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(db.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
        }

        db.show();
    }

    private void showDialog(String msg) {
        AlertDialog db = new MaterialAlertDialogBuilder(getContext).create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(db.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
        }
        db.setTitle("INFO");
        db.setMessage(msg);
        db.show();
    }

    private class lolTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Client client = new Client(urls[0]);
            try {
                int responseCode = client.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject json = client.getJson();
                    return json.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException | JSONException e) {
                new lolTask2().execute("https://raw.githubusercontent.com/ZNFDev/Useless/refs/heads/main/lol.json");
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                sexData = new JSONObject(result);
            } catch (JSONException e) {
                Toast.makeText(getContext, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}


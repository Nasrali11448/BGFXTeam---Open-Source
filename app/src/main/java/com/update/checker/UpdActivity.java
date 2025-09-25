package com.update.checker;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.content.Context;
import android.app.AlertDialog;
import android.os.Build;
import java.util.Objects;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.AsyncTask;
import java.net.HttpURLConnection;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import com.executor.material.widgets.MaterialAlertDialogBuilder;

public class UpdActivity extends Activity {
    Context getContext;
    WebView webView;
    private JSONObject sexData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //new lolTask2().execute("https://raw.githubusercontent.com/ZNFDev/Useless/refs/heads/main/lol.json");
        webView = new WebView(this);
        Intent i = new Intent();
        setContentView(webView);
        getContext = this;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.equals("https://zev.com/done")) {
                        long currentTime = System.currentTimeMillis();
                        SharedPreferences prefs = getContext.getSharedPreferences("com.sandboxol.blockymods.official_preferences", getContext.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong("play.time", currentTime);
                        editor.apply();
                        Toast.makeText(UpdActivity.this, "Access granted for 24 hours!", 1).show();
                        showDialog("Access Granted for 24 hours!");
                        /*Intent launchIntent = getContext.getPackageManager().getLaunchIntentForPackage("net.resquared.ultramods");
                         startActivity(launchIntent);*/
                        finish();
                        return true;
                    }
                    return false;
                }
            });
        try {
            //webView.loadUrl("https://vnshortener.com/blockymods-menu-access");
            webView.loadUrl(getIntent().getStringExtra("earnLink"));
        } catch(Exception e) {

        }
        /*LinearLayout layout = new LinearLayout(this);
         TextView text = new TextView(this);
         text.setText("Bruh");
         layout.addView(text);
         setContentView(layout);*/
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            showDialog("AYEEE MAN, let me earn some money for my hardwork");
            //toast.showToast("you can go back only if you completed the task!", false);
        }
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



}


package com.retailos.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.*;
import android.widget.Toast;

public class MainActivity extends Activity {
    WebView web;
    static final int REQ_CAMERA = 10;
    static final int REQ_SCAN = 20;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        setContentView(web);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        web.setWebChromeClient(new WebChromeClient(){
            @Override public void onPermissionRequest(PermissionRequest r){
                runOnUiThread(() -> r.grant(r.getResources()));
            }
        });
        web.addJavascriptInterface(new AndroidBridge(), "Android");
        web.loadUrl("file:///android_asset/index.html");
        if (android.os.Build.VERSION.SDK_INT >= 23 &&
            checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
    }

    public class AndroidBridge {
        @JavascriptInterface public void startScanner() {
            if (android.os.Build.VERSION.SDK_INT >= 23 &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
                return;
            }
            startActivityForResult(new Intent(MainActivity.this, ScannerActivity.class), REQ_SCAN);
        }
        @JavascriptInterface public void openWhatsApp(String phone, String text) {
            try {
                String clean = phone == null ? "" : phone.replaceAll("\\D", "");
                if (clean.length() == 10) clean = "91" + clean;
                Uri uri = Uri.parse("https://wa.me/" + clean + "?text=" + Uri.encode(text));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "WhatsApp open nahi hua", Toast.LENGTH_SHORT).show());
            }
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SCAN && resultCode == RESULT_OK && data != null) {
            String code = data.getStringExtra("barcode");
            if (code != null) {
                String js = "window.onNativeBarcode(" + org.json.JSONObject.quote(code) + ");";
                web.evaluateJavascript(js, null);
            }
        }
    }

    @Override public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
}

package com.example.grouptimetable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        //webview
        WebView myWebView = (WebView) findViewById(R.id.webview);
        //settings
        WebSettings settings = myWebView.getSettings();
        //increase font size
        settings.setTextZoom(150);
        //responsive settings
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //load html file
        myWebView.loadUrl("file:///android_asset/userguide1.html");
    }
}

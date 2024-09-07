package com.alibaba.android.arouter.demo.module1;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.arouter.module.java.R;

@Route(path = "/module_java/webview")
public class TestWebview extends Activity {

    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_webview);


        webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl(getIntent().getStringExtra("url"));
    }
}

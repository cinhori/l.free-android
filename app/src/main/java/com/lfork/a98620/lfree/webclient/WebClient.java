package com.lfork.a98620.lfree.webclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lfork.a98620.lfree.R;

import java.util.Objects;

public class WebClient extends AppCompatActivity {


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_client_act);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        setupActionBar();
        setTitle(url);
        if (url != null) {
            WebView webView = findViewById(R.id.webview);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            WebSettings settings = webView.getSettings();
            settings.setLoadWithOverviewMode(true);
            webView.setWebViewClient(new WebClient.MyWebViewClient(webView));
            webView.loadUrl(url);
        }
    }

    private void setupActionBar(){
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true
        // 有小箭头，并且图标可以点击
        actionBar.setDisplayShowHomeEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setTitle(String string){
        Objects.requireNonNull(getSupportActionBar()).setTitle(string);
    }

    public static void openUrlInWebClient(Context context,String url){
        Intent intent = new Intent(context, WebClient.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    private class MyWebViewClient extends WebViewClient {
        private WebView webView;

        MyWebViewClient(WebView webView) {
            this.webView = webView;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            imgReset();//重置webview中img标签的图片大小  以适应手机的宽度
            addBlock();//屏蔽掉无用的标签
            setTitle(view.getTitle());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        /**
         * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
         **/
        private void imgReset() {
            webView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName('img'); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "var img = objs[i];   " +
                    "    img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
                    "}" +
                    "})()");
        }

        private void addBlock(){
            webView.loadUrl("javascript:(function(){document.getElementById(\"logo\").style.display=\"none\";})()");

        }
    }

}
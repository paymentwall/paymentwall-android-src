package com.paymentwall.sdk.pwlocal.ui;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PwLocalWebViewClient extends WebViewClient{
    private WebViewCallBack webViewCallBack;

    public PwLocalWebViewClient() {
    }

    public PwLocalWebViewClient(WebViewCallBack webViewCallBack) {
        this.webViewCallBack = webViewCallBack;
    }

    public WebViewCallBack getWebViewCallBack() {
        return webViewCallBack;
    }

    public void setWebViewCallBack(WebViewCallBack webViewCallBack) {
        this.webViewCallBack = webViewCallBack;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        if(webViewCallBack != null) webViewCallBack.webviewLoadResource(this, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if(webViewCallBack != null) webViewCallBack.webviewReceivedError(this, view, errorCode, description, failingUrl);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(webViewCallBack != null) return webViewCallBack.webviewShouldOverrideUrlLoading(this, url);
        else return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if(webViewCallBack != null) webViewCallBack.webviewPageStarted(this);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(webViewCallBack != null) webViewCallBack.webviewPageFinished(this);
    }

    public interface WebViewCallBack {
        void webviewLoadResource(WebViewClient webViewClient, String url);
        void webviewReceivedError(WebViewClient webViewClient, WebView view, int errorCode, String description, String failingUrl);
        void webviewPageStarted(WebViewClient webViewClient);
        void webviewPageFinished(WebViewClient webViewClient);
        boolean webviewShouldOverrideUrlLoading(WebViewClient webViewClient, String url);
    }




}

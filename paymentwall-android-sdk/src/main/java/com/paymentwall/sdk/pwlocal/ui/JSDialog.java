package com.paymentwall.sdk.pwlocal.ui;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.paymentwall.pwunifiedsdk.util.MiscUtils;

/**
 * Created by harvey on 4/25/17.
 */

public class JSDialog extends DialogFragment implements  PwLocalWebViewClient.WebViewCallBack {
    public static final String KEY_RESULT_MSG = "key_result_msg";
    public static final String KEY_URL = "key_window_url";
    public static final String SUCCESS_URL = "success_url";
    String successfulUrl = null;
    WebView dialogWv;
    ImageView backBtn;
    ProgressWheel progressBar;
    FrameLayout fakeToolbar;
    LinearLayout outerContainer;
    SuccessUrlListener successUrlListener;

    WebView.WebViewTransport transport;

    public SuccessUrlListener getSuccessUrlListener() {
        return successUrlListener;
    }

    public void setSuccessUrlListener(SuccessUrlListener successUrlListener) {
        this.successUrlListener = successUrlListener;
    }

    public static JSDialog newInstance(Message resultMsg, String successfulUrl) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_RESULT_MSG, resultMsg);
        if(!TextUtils.isEmpty(successfulUrl)) args.putString(SUCCESS_URL, successfulUrl);
        JSDialog fragment = new JSDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = initView(getActivity());
        return v;
    }

    private View initView(Context context) {
        outerContainer = new LinearLayout(context);
        outerContainer.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        outerContainer.setLayoutParams(layoutParams);
        fakeToolbar = new FrameLayout(context);
        progressBar = new ProgressWheel(context);
        float dpFactor = context.getResources().getDisplayMetrics().densityDpi / 160f;

        backBtn = new ImageView(context);
        backBtn.setScaleType(ImageView.ScaleType.CENTER);
        backBtn.setImageDrawable(ShapeUtils.getBackButtonDrawable(0xff000000, (int)(40 * dpFactor), (int)(40 * dpFactor)));
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            backBtn.setBackgroundDrawable(ShapeUtils.getButtonBackground(0xffffbb33, 0xffff8800));
        } else {
            backBtn.setBackground(ShapeUtils.getButtonBackground(0xffffbb33, 0xffff8800));
        }

        LinearLayout.LayoutParams fakeToolbarLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (40*dpFactor));
        fakeToolbar.setLayoutParams(fakeToolbarLP);
        fakeToolbar.setBackgroundColor(0xffffbb33);

        FrameLayout.LayoutParams backBtnLP = new FrameLayout.LayoutParams((int) (40*dpFactor), (int) (40*dpFactor));
        backBtnLP.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        backBtn.setLayoutParams(backBtnLP);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogWv == null) return;
                dismiss();
            }
        });

        FrameLayout.LayoutParams progressBarLP = new FrameLayout.LayoutParams((int) (40*dpFactor), (int) (40*dpFactor));
        progressBarLP.rightMargin = (int) (16 * dpFactor);
        progressBarLP.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        progressBar.setLayoutParams(progressBarLP);
        progressBar.setBarWidth((int) (2 * dpFactor));
        progressBar.setBarColor(0xff000000);
        progressBar.setCircleRadius((int) (14*dpFactor));
        progressBar.setVisibility(View.GONE);

        fakeToolbar.addView(backBtn);
        fakeToolbar.addView(progressBar);
        outerContainer.addView(fakeToolbar);

        dialogWv = new WebView(context);
        LinearLayout.LayoutParams wvLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialogWv.setLayoutParams(wvLP);
        outerContainer.addView(dialogWv);

        return outerContainer;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWebView();
        if (getArguments() == null) return;

        successfulUrl = getArguments().getString(SUCCESS_URL);
        
        if (getArguments().containsKey(KEY_RESULT_MSG)) {
            Message resultMsg = getArguments().getParcelable(KEY_RESULT_MSG);
            if (resultMsg == null) dismiss();
            if (resultMsg.obj != null) {
                transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(dialogWv);
            }
            resultMsg.sendToTarget();
            getArguments().remove(KEY_RESULT_MSG);
        } else {
            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_URL))
                dialogWv.loadUrl(savedInstanceState.getString(KEY_URL));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialogWv.getUrl() != null) outState.putString(KEY_URL, dialogWv.getUrl());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.spin();
    }

    private void stopLoading() {
        progressBar.stopSpinning();
        progressBar.setVisibility(View.GONE);
    }

    protected void initWebView() {
        dialogWv.getSettings().setJavaScriptEnabled(true);
        dialogWv.getSettings().setDomStorageEnabled(true);
        dialogWv.getSettings().setSupportZoom(true);
        dialogWv.getSettings().setBuiltInZoomControls(true);
        dialogWv.getSettings().setSupportMultipleWindows(true);
        dialogWv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        MiscUtils.removeJsInterface(dialogWv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(false);
        }
        CookieManager.getInstance().setAcceptCookie(true);
        compatSetAccept3rdPartyCookie(dialogWv, true);
        dialogWv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onCloseWindow(WebView window) {
                dismiss();
            }
        });
        PwLocalWebViewClient pwLocalWebViewClient = new PwLocalWebViewClient();
        pwLocalWebViewClient.setWebViewCallBack(this);
        dialogWv.setWebViewClient(pwLocalWebViewClient);
        /*dialogWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if(MiscUtils.isSuccessfulUrl(failingUrl, successfulUrl)) {
                    dismiss();
                    if(successUrlListener != null) successUrlListener.onSuccessUrlLinkOpened(JSDialog.this);
                } else {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                if(getGooglePlayLink(url) != null) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        Activity host = (Activity) view.getContext();
                        host.startActivity(intent);
                        JSDialog.this.dismiss();
                        return true;
                    } catch (ActivityNotFoundException e) {
                        // Google Play app is not installed, open the app store link
                        Uri uri = Uri.parse(url);
                        view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                        return false;
                    }
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                startLoading();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                stopLoading();
            }
        });*/

    }

    @Override
    public void webviewLoadResource(WebViewClient webViewClient, String url) {
        // do nothing
    }

    @Override
    public void webviewReceivedError(WebViewClient webViewClient, WebView view, int errorCode, String description, String failingUrl) {
        if(MiscUtils.isSuccessfulUrl(failingUrl, successfulUrl)) {
            dismiss();
            if(successUrlListener != null) successUrlListener.onSuccessUrlLinkOpened(JSDialog.this);
        } else {
            webViewClient.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @Override
    public void webviewPageStarted(WebViewClient webViewClient) {
        startLoading();
    }

    @Override
    public void webviewPageFinished(WebViewClient webViewClient) {
        stopLoading();
    }

    @Override
    public boolean webviewShouldOverrideUrlLoading(WebViewClient webViewClient, String url) {
        if(MiscUtils.isSuccessfulUrl(url, successfulUrl)) {
            dismiss();
            if(successUrlListener != null) successUrlListener.onSuccessUrlLinkOpened(JSDialog.this);
        } else if(shouldOpenInApp(url)) {
            dialogWv.loadUrl(url);
            return true;
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                dialogWv.loadUrl(url);
                return false;
            }
        }
        /*if(getGooglePlayLink(url) != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                if(getActivity()!=null) startActivity(intent);
                JSDialog.this.dismiss();
                return true;
            } catch (ActivityNotFoundException e) {
                // Google Play app is not installed, open the app store link
                Uri uri = Uri.parse(url);
                dialogWv.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                return false;
            }
        }*/
        return false;
    }

    private boolean shouldOpenInApp(String url) {
        return (url!=null && (url.startsWith("http") || url.startsWith("https")));
    }

    public interface SuccessUrlListener {
        void onSuccessUrlLinkOpened(JSDialog jsDialog);
    }

    private void compatSetAccept3rdPartyCookie(WebView webView, boolean accept) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, accept);
        }
    }
}

package com.paymentwall.sdk.pwlocal.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.paymentwall.pwunifiedsdk.util.PwUtils;
import com.paymentwall.sdk.pwlocal.message.CustomRequest;
import com.paymentwall.sdk.pwlocal.message.LocalDefaultRequest;
import com.paymentwall.sdk.pwlocal.message.LocalFlexibleRequest;
import com.paymentwall.sdk.pwlocal.message.LocalRequest;
import com.paymentwall.sdk.pwlocal.message.MultiPaymentStatusException;
import com.paymentwall.sdk.pwlocal.message.PaymentStatus;
import com.paymentwall.sdk.pwlocal.message.PaymentStatusRequest;
import com.paymentwall.sdk.pwlocal.utils.ApiType;
import com.paymentwall.sdk.pwlocal.utils.Const;
import com.paymentwall.sdk.pwlocal.utils.Const.PW_URL;
import com.paymentwall.sdk.pwlocal.utils.Key;
import com.paymentwall.sdk.pwlocal.utils.PaymentMethod;
import com.paymentwall.sdk.pwlocal.utils.PaymentStatusComplexCallback;
import com.paymentwall.sdk.pwlocal.utils.PaymentStatusUtils;
import com.paymentwall.sdk.pwlocal.utils.PwLocalMiscUtils;
import com.paymentwall.sdk.pwlocal.utils.ResponseCode;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static com.paymentwall.pwunifiedsdk.util.PwUtils.HTTP_X_APP_NAME;
import static com.paymentwall.pwunifiedsdk.util.PwUtils.HTTP_X_APP_SIGNATURE;
import static com.paymentwall.pwunifiedsdk.util.PwUtils.HTTP_X_INSTALL_TIME;
import static com.paymentwall.pwunifiedsdk.util.PwUtils.HTTP_X_PACKAGE_CODE;
import static com.paymentwall.pwunifiedsdk.util.PwUtils.HTTP_X_PACKAGE_NAME;
import static com.paymentwall.pwunifiedsdk.util.PwUtils.HTTP_X_PERMISSON;
import static com.paymentwall.pwunifiedsdk.util.PwUtils.HTTP_X_UPDATE_TIME;
import static com.paymentwall.pwunifiedsdk.util.PwUtils.HTTP_X_VERSION_NAME;

public class PwLocalActivity extends FragmentActivity implements
//        PwLocalJsInterface.Callback,
        JSDialog.SuccessUrlListener,
        PaymentStatusComplexCallback {
    public static final String TAG_WEB_DIALOG = "WebDialog";
    public static final int REQUEST_CODE = 0x8087;
    //    private static final String TAG = "PwLocalWebActivity";
    private static float dpFactor = 1f;
    private WebView rootWebView;
    private ImageView backBtn;
    private LinearLayout outerContainer;
    private ProgressWheel progressBar;
    private FrameLayout fakeToolbar;

    private String url;
    private LocalRequest message;
    private StateListDrawable btnBackground;
    private boolean enablePaymentStatus;
    private CustomRequest customParameters;
    private String successfulUrl = Const.DEFAULT_SUCCESS_URL;
    private boolean customRequestMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preInitUi();
        initUi();
        acquireMessage();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void finish() {
        if (fakeToolbar != null) {
            fakeToolbar.removeAllViews();
        }
        if (outerContainer != null) {
            outerContainer.removeAllViews();
        }
        if (rootWebView != null) {
            rootWebView.removeAllViews();
            rootWebView.destroy();
        }
        outerContainer = null;
        btnBackground = null;
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setVisible(false);
    }

    protected void acquireMessage() {
        Map<String, String> extraHeaders = getAppParametersFull(this);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Intent result = new Intent();
            result.putExtra(Key.SDK_ERROR_MESSAGE, "NULL REQUEST_TYPE");
            errorRespond(result);
        }
        if (extras.containsKey(Key.ENABLE_PAYMENT_STATUS)) {
            enablePaymentStatus = extras.getBoolean(Key.ENABLE_PAYMENT_STATUS, false);
        }
        if (extras.containsKey(Key.CUSTOM_REQUEST_MAP) && extras.containsKey(Key.CUSTOM_REQUEST_TYPE)) {
            customRequestMode = true;
            try {
                customParameters = extras.getParcelable(Key.CUSTOM_REQUEST_MAP);
                if (customParameters.containsKey(Const.P.SUCCESS_URL)) {
                    successfulUrl = customParameters.get(Const.P.SUCCESS_URL);
                } else {
                    customParameters.put(Const.P.SUCCESS_URL, successfulUrl);
                }
                String customRequestType = extras.getString(Key.CUSTOM_REQUEST_TYPE);
                String rootUrl;
                if (customRequestType.equals(ApiType.VIRTUAL_CURRENCY)) {
                    rootUrl = PW_URL.PS;
                } else if (customRequestType.equals(ApiType.CART)) {
                    rootUrl = PW_URL.CART;
                } else if (customRequestType.equals(ApiType.DIGITAL_GOODS)) {
                    rootUrl = PW_URL.SUBSCRIPTION;
                } else {
                    Intent result = new Intent();
                    result.putExtra(Key.SDK_ERROR_MESSAGE, "MESSAGE ERROR");
                    errorRespond(result);
                    return;
                }
                String query = customParameters.getUrlParam();
                this.url = rootUrl + query;
                if (rootWebView != null) {
                    if (customParameters.getMobileDownloadLink() != null)
                        extraHeaders.put(Const.P.HISTORY_MOBILE_DOWNLOAD_LINK, customParameters.getMobileDownloadLink());
//                    Log.i("PWLOCAL_URL", url);
                    rootWebView.loadUrl(url, extraHeaders);
                    addJsHandle();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Intent result = new Intent();
                result.putExtra(Key.SDK_ERROR_MESSAGE, "MESSAGE ERROR");
                errorRespond(result);
                return;
            }
        } else {
            customRequestMode = false;
            int requestType = extras.getInt(Key.PAYMENT_TYPE,
                    PaymentMethod.NULL);
            if (requestType == PaymentMethod.NULL) {
                // Show error
                Intent result = new Intent();
                result.putExtra(Key.SDK_ERROR_MESSAGE, "NULL REQUEST_TYPE");
                errorRespond(result);
            } else { // Has request code
                if (requestType == PaymentMethod.PW_LOCAL_DEFAULT
                        || requestType == PaymentMethod.PW_LOCAL_FLEXIBLE) {
                    String rootUrl = "";
                    if (getIntent().hasExtra(Key.PWLOCAL_REQUEST_MESSAGE)) {
                        message = getIntent().getParcelableExtra(Key.PWLOCAL_REQUEST_MESSAGE);
                    } else if (getIntent().hasExtra(Key.REQUEST_MESSAGE)) {
                        message = (LocalRequest) getIntent().getSerializableExtra(
                                Key.REQUEST_MESSAGE);
                    }

                    if (message.getSuccessUrl() != null) {
                        successfulUrl = message.getSuccessUrl();
                    } else {
                        message.setSuccessUrl(successfulUrl);
                    }
                    if (message != null) {
                        if (requestType == PaymentMethod.PW_LOCAL_DEFAULT) {
                            // Display Default widget
                            if (message instanceof LocalDefaultRequest) {
                                if (message.getApiType().equals(ApiType.PS)) {
                                    rootUrl = PW_URL.PS;
                                } else if (message.getApiType().equals(ApiType.CART)) {
                                    rootUrl = PW_URL.CART;
                                } else if (message.getApiType().equals(ApiType.SUBSCRIPTION)) {
                                    rootUrl = PW_URL.SUBSCRIPTION;
                                } else {
                                    Intent result = new Intent();
                                    result.putExtra(Key.SDK_ERROR_MESSAGE, "PWLocal flexible wrong message type");
                                    errorRespond(result);
                                    return;
                                }

                                this.url = message.getUrl(rootUrl);
                                if (rootWebView != null) {
                                    if (message.getMobileDownloadLink() != null)
                                        extraHeaders.put(Const.P.HISTORY_MOBILE_DOWNLOAD_LINK, message.getMobileDownloadLink());

                                    rootWebView.loadUrl(url, extraHeaders);
                                    addJsHandle();
                                }
                            } else {
                                Intent result = new Intent();
                                result.putExtra(Key.SDK_ERROR_MESSAGE, "PWLocal default wrong message type");
                                errorRespond(result);
                            }
                        } else if (requestType == PaymentMethod.PW_LOCAL_FLEXIBLE) {
                            // Display Flexible widget
                            if (message instanceof LocalFlexibleRequest) {
                                if (message.getApiType().equals(ApiType.PS)) {
                                    rootUrl = PW_URL.PS;
                                } else if (message.getApiType().equals(ApiType.CART)) {
                                    rootUrl = PW_URL.CART;
                                } else if (message.getApiType().equals(ApiType.SUBSCRIPTION)) {
                                    rootUrl = PW_URL.SUBSCRIPTION;
                                } else {
                                    Intent result = new Intent();
                                    result.putExtra(Key.SDK_ERROR_MESSAGE, "PWLocal flexible wrong message type");
                                    errorRespond(result);
                                    return;
                                }
                                this.url = message.getUrl(rootUrl);
                                if (rootWebView != null) {
                                    if (message.getMobileDownloadLink() != null)
                                        extraHeaders.put(Const.P.HISTORY_MOBILE_DOWNLOAD_LINK, message.getMobileDownloadLink());

                                    rootWebView.loadUrl(url, extraHeaders);
                                    addJsHandle();
                                }
                            } else {
                                Intent result = new Intent();
                                result.putExtra(Key.SDK_ERROR_MESSAGE, "PWLocal flexible wrong message type");
                                errorRespond(result);
                                return;
                            }
                        }
                    } else {
                        Intent result = new Intent();
                        result.putExtra(Key.SDK_ERROR_MESSAGE, "NULL MESSAGE");
                        errorRespond(result);
                        return;
                    }
                } else {
                    Intent result = new Intent();
                    result.putExtra(Key.SDK_ERROR_MESSAGE, "MESSAGE ERROR");
                    errorRespond(result);
                    return;
                }
            }
        }
    }

    protected void preInitUi() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dpFactor = getResources().getDisplayMetrics().densityDpi / 160f;
    }

    protected void addJsHandle() {
        /*if (rootWebView != null) {
            rootWebView
                    .addJavascriptInterface(new PwLocalJsInterface(this), "Android");
        }*/
    }

    @Override
    public void onSuccessUrlLinkOpened(JSDialog jsDialog) {
        onPWLocalCallback();
    }

    //    @Override
    public void onPWLocalCallback() {
        setResult(ResponseCode.SUCCESSFUL);
        if (enablePaymentStatus
                && message != null
                && message instanceof LocalFlexibleRequest
                && (message.findParameter(Const.P.AG_EXTERNAL_ID) != null
                && message.findParameter(Const.P.KEY) != null
                && message.findParameter(Const.P.UID) != null)) {
            progressBar.setVisibility(View.VISIBLE);
            String key = message.findParameter(Const.P.KEY);
            String uid = message.findParameter(Const.P.UID);
            String agExternalId = message.findParameter(Const.P.AG_EXTERNAL_ID);
            PaymentStatusRequest paymentStatusRequest = null;
            if (message.getSecretKey() != null) {
                if (message.getSignVersion() == 2 || message.getSignVersion() == 3) {
                    paymentStatusRequest = new PaymentStatusRequest.Builder()
                            .setQuery(key, uid, agExternalId, message.getSecretKey(), message.getSignVersion())
                            .build();
                } else {
                    paymentStatusRequest = new PaymentStatusRequest.Builder()
                            .setQuery(key, uid, agExternalId, message.getSecretKey(), 3)
                            .build();
                }
            } else {
                paymentStatusRequest = new PaymentStatusRequest.Builder()
                        .setQuery(key, uid, agExternalId)
                        .build();
            }
            try {
                PaymentStatusUtils.getPaymentStatus(paymentStatusRequest, this);
            } catch (Exception e) {
                e.printStackTrace();
                successRespond();
            }
        } else if (enablePaymentStatus) {
            if (customParameters != null
                    && customParameters.containsKey(Const.P.AG_EXTERNAL_ID)
                    && customParameters.containsKey(Const.P.KEY)
                    && customParameters.containsKey(Const.P.UID)) {
                progressBar.setVisibility(View.VISIBLE);
                String key = customParameters.get(Const.P.KEY);
                String uid = customParameters.get(Const.P.UID);
                String agExternalId = customParameters.get(Const.P.AG_EXTERNAL_ID);
                PaymentStatusRequest paymentStatusRequest = new PaymentStatusRequest.Builder()
                        .setQuery(key, uid, agExternalId)
                        .build();
                try {
                    PaymentStatusUtils.getPaymentStatus(paymentStatusRequest, this);
                } catch (Exception e) {
                    e.printStackTrace();
                    successRespond();
                }
            } else {
                successRespond();
            }

        } else {
            successRespond();
        }
    }

    public void cancelRespond() {
        Intent intent = new Intent();
        if (message == null) {
            if (getIntent().hasExtra(Key.PWLOCAL_REQUEST_MESSAGE)) {
                message = getIntent().getParcelableExtra(Key.PWLOCAL_REQUEST_MESSAGE);
            } else if (getIntent().hasExtra(Key.REQUEST_MESSAGE)) {
                message = (LocalRequest) getIntent().getSerializableExtra(
                        Key.REQUEST_MESSAGE);
            }
        }

        if (customParameters == null) {
            if (getIntent().hasExtra(Key.CUSTOM_REQUEST_MAP)) {
                customParameters = getIntent().getParcelableExtra(Key.CUSTOM_REQUEST_MAP);
            }
        }

        if (message != null) {
            intent.putExtra(Key.PWLOCAL_REQUEST_MESSAGE, (Parcelable) message);
        } else if (customParameters != null) {
            intent.putExtra(Key.CUSTOM_REQUEST_MAP, customParameters);
        }

        setResult(ResponseCode.CANCEL, intent);
        finish();
    }

    public void errorRespond(Intent intent) {

        if (message == null) {
            if (getIntent().hasExtra(Key.PWLOCAL_REQUEST_MESSAGE)) {
                message = getIntent().getParcelableExtra(Key.PWLOCAL_REQUEST_MESSAGE);
            } else if (getIntent().hasExtra(Key.REQUEST_MESSAGE)) {
                message = (LocalRequest) getIntent().getSerializableExtra(
                        Key.REQUEST_MESSAGE);
            }
        }

        if (customParameters == null) {
            if (getIntent().hasExtra(Key.CUSTOM_REQUEST_MAP)) {
                customParameters = getIntent().getParcelableExtra(Key.CUSTOM_REQUEST_MAP);
            }
        }

        if (message != null) {
            intent.putExtra(Key.PWLOCAL_REQUEST_MESSAGE, (Parcelable) message);
        } else if (customParameters != null) {
            intent.putExtra(Key.CUSTOM_REQUEST_MAP, customParameters);
        }

        setResult(ResponseCode.ERROR, intent);
        finish();
    }

    public void successRespond(Intent intent) {
        if (message != null) {
            intent.putExtra(Key.PWLOCAL_REQUEST_MESSAGE, (Parcelable) message);
        } else if (customParameters != null) {
            intent.putExtra(Key.CUSTOM_REQUEST_MAP, customParameters);
        }
        setResult(ResponseCode.SUCCESSFUL, intent);
        finish();
    }

    public void errorRespond() {
        Intent intent = new Intent();
        if (message == null) {
            if (getIntent().hasExtra(Key.PWLOCAL_REQUEST_MESSAGE)) {
                message = getIntent().getParcelableExtra(Key.PWLOCAL_REQUEST_MESSAGE);
            } else if (getIntent().hasExtra(Key.REQUEST_MESSAGE)) {
                message = (LocalRequest) getIntent().getSerializableExtra(
                        Key.REQUEST_MESSAGE);
            }
        }

        if (customParameters == null) {
            if (getIntent().hasExtra(Key.CUSTOM_REQUEST_MAP)) {
                customParameters = getIntent().getParcelableExtra(Key.CUSTOM_REQUEST_MAP);
            }
        }

        if (message != null) {
            intent.putExtra(Key.PWLOCAL_REQUEST_MESSAGE, (Parcelable) message);
        } else if (customParameters != null) {
            intent.putExtra(Key.CUSTOM_REQUEST_MAP, customParameters);
        }
        setResult(ResponseCode.ERROR, intent);
        finish();
    }

    public void successRespond() {
        Intent intent = new Intent();
        if (message != null) {
            intent.putExtra(Key.PWLOCAL_REQUEST_MESSAGE, (Parcelable) message);
        } else if (customParameters != null) {
            intent.putExtra(Key.CUSTOM_REQUEST_MAP, customParameters);
        }
        successRespond(intent);
    }

    @Override
    public void onError(Exception error) {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Key.PAYMENT_STATUS_EXCEPTION, error);
        bundle.putBoolean(Key.PAYMENT_STATUS_IS_SUCCESSFUL, false);
        intent.putExtra(Key.RESULT_PAYMENT_STATUS, bundle);
        successRespond(intent);
    }

    @Override
    public void onSuccess(List<PaymentStatus> paymentStatusList) {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Key.PAYMENT_STATUS_EXCEPTION, new MultiPaymentStatusException("Got more than 1 payment status"));
        bundle.putBoolean(Key.PAYMENT_STATUS_IS_SUCCESSFUL, false);
        intent.putExtra(Key.RESULT_PAYMENT_STATUS, bundle);
        successRespond(intent);
    }

    @Override
    public void onSuccessSingle(PaymentStatus paymentStatus) {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Key.PAYMENT_STATUS_MESSAGE, paymentStatus);
        bundle.putBoolean(Key.PAYMENT_STATUS_IS_SUCCESSFUL, true);
        intent.putExtra(Key.RESULT_PAYMENT_STATUS, bundle);
        successRespond(intent);
    }

    @SuppressLint("NewApi")
    protected void initUi() {
        if (backBtn == null)
            backBtn = new ImageView(this);
        if (outerContainer == null)
            outerContainer = new LinearLayout(this);
        if (progressBar == null)
            progressBar = new ProgressWheel(this);

        if (backBtn.getId() == View.NO_ID)
            backBtn.setId(1000 + new Random().nextInt(999));
        if (outerContainer.getId() == View.NO_ID)
            outerContainer.setId(2000 + new Random().nextInt(999));

        outerContainer.removeAllViews();

        outerContainer = new LinearLayout(this);
        outerContainer.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        outerContainer.setLayoutParams(layoutParams);
        fakeToolbar = new FrameLayout(this);
        progressBar = new ProgressWheel(this);
        float dpFactor = getResources().getDisplayMetrics().densityDpi / 160f;

        backBtn = new ImageView(this);
        FrameLayout.LayoutParams backBtnLP = new FrameLayout.LayoutParams((int) (40 * dpFactor), (int) (40 * dpFactor));
        backBtnLP.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        backBtn.setLayoutParams(backBtnLP);
        backBtn.setScaleType(ImageView.ScaleType.CENTER);
        backBtn.setImageDrawable(ShapeUtils.getBackButtonDrawable(0xff000000, (int) (40 * dpFactor), (int) (40 * dpFactor)));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            backBtn.setBackgroundDrawable(ShapeUtils.getButtonBackground(0xffffbb33, 0xffff8800));
        } else {
            backBtn.setBackground(ShapeUtils.getButtonBackground(0xffffbb33, 0xffff8800));
        }

        LinearLayout.LayoutParams fakeToolbarLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (40 * dpFactor));
        fakeToolbar.setLayoutParams(fakeToolbarLP);
        fakeToolbar.setBackgroundColor(0xffffbb33);

        FrameLayout.LayoutParams progressBarLP = new FrameLayout.LayoutParams((int) (40 * dpFactor), (int) (40 * dpFactor));
        progressBarLP.rightMargin = (int) (16 * dpFactor);
        progressBarLP.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        progressBar.setLayoutParams(progressBarLP);
        progressBar.setBarWidth((int) (2 * dpFactor));
        progressBar.setBarColor(0xff000000);
        progressBar.setCircleRadius((int) (14 * dpFactor));
        progressBar.setVisibility(View.GONE);

        fakeToolbar.addView(backBtn);
        fakeToolbar.addView(progressBar);
        outerContainer.addView(fakeToolbar);

        setContentView(outerContainer);

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRespond();
            }
        });

        initWebView();
        outerContainer.addView(rootWebView);
    }

    private void initWebView() {
        if (rootWebView == null) {
            rootWebView = new WebView(this);

            if (rootWebView.getId() == View.NO_ID)
                rootWebView.setId(new Random().nextInt(999));
            rootWebView.getSettings().setJavaScriptEnabled(true);
            rootWebView.getSettings().setDomStorageEnabled(true);

            removeJsInterface(rootWebView);
            rootWebView.getSettings().setSupportZoom(true);
            rootWebView.getSettings().setBuiltInZoomControls(true);
            rootWebView.getSettings().setSupportMultipleWindows(true);
            rootWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rootWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(false);
            }
            CookieManager.getInstance().setAcceptCookie(true);
            compatSetAccept3rdPartyCookie(rootWebView, true);
            LinearLayout.LayoutParams wvLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootWebView.setLayoutParams(wvLP);
            rootWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                    JSDialog webDialogFragment = JSDialog.newInstance(resultMsg, successfulUrl);
                    webDialogFragment.show(getSupportFragmentManager(), TAG_WEB_DIALOG);
                    webDialogFragment.setSuccessUrlListener(PwLocalActivity.this);
                    return true;
                }
            });
            rootWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onLoadResource(final WebView view, String url) {
                    super.onLoadResource(view, url);
//                    Log.i("PWLocal", "onLoadResource url = " + url);
                    if (isSuccessful(url)) {
                        PwLocalActivity.this.onPWLocalCallback();
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {

//                    Log.i("PWLocal", "onReceivedError failingUrl = " + failingUrl);
                    if (isFpLink(failingUrl)) return;
                    if (!isSuccessful(failingUrl)) {
                        // Handle the error
                        Toast.makeText(PwLocalActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra(Key.SDK_ERROR_MESSAGE, "CONNECTION ERROR");
                        errorRespond(intent);
                        finish();
                    } else {

                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (isFpLink(url)) return true;
                    if (getGooglePlayLink(url) != null) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            Activity host = (Activity) view.getContext();
                            host.startActivity(intent);
                            return true;
                        } catch (ActivityNotFoundException e) {
                            // Google Play app is not installed, open the app store link
                            Uri uri = Uri.parse(url);
                            view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                            return false;
                        }
                    }
                    if (!isSuccessful(url)) {
                        return false;
                    } else {
                        PwLocalActivity.this.onPWLocalCallback();
                        return false;
                    }
                }

                @Override
                public void onPageStarted(WebView view, String url,
                                          Bitmap favicon) {
//                    Log.i("PWLocal", "onPageStarted url = " + url);
                    startLoading();
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    stopLoading();
                    super.onPageFinished(view, url);
                }
            });

        }
    }

    private void startLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.spin();
        }
    }

    private void stopLoading() {
        if (progressBar != null) {
            progressBar.stopSpinning();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (rootWebView != null) {
            outerContainer.removeView(rootWebView);
        }
        super.onConfigurationChanged(newConfig);
        initUi();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        rootWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rootWebView.restoreState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showError(int errorCode, String message) {

    }

    public static float dpToPx(float dpValue) {
        return dpValue * dpFactor;
    }

    public static int dpToPxInt(float dpValue) {
        return (int) (dpValue * dpFactor);
    }

    public static float pxToDp(float pxValue) {
        return pxValue / dpFactor;
    }

    public static int pxToDpInt(float pxValue) {
        return (int) (pxValue / dpFactor);
    }

    @Override
    public void onBackPressed() {
        cancelRespond();
        super.onBackPressed();
    }

    public boolean isSuccessful(String url) {
        if (successfulUrl != null) {
            Uri successUri = Uri.parse(successfulUrl);
            Uri uri = Uri.parse(url);
            if (successUri == null || uri == null) return false;

            return (
                    uri.getHost().equals(successUri.getHost()) &&
                            uri.getScheme().equals(successUri.getScheme())
            );
        } else {
            Uri uri = Uri.parse(url);
            return (uri != null && "pwlocal".equals(uri.getScheme()) && "paymentsuccessful".equals(uri.getHost()));
        }
    }

    protected void removeJsInterface(WebView webView) {
        try {
            Method method = webView.getClass().getMethod(
                    "removeJavascriptInterface", String.class);
            if (method != null) {
                method.invoke(webView, "searchBoxJavaBridge_");
                method.invoke(webView, "accessibility");
            }
        } catch (Exception ex) {
//            Log.i(LOGTAG, ex.toString());
        }
    }

    public static Map<String, String> getAppParameters(Context context) {
        TreeMap<String, String> parameters = new TreeMap<String, String>();
        try {
            Context appContext = context.getApplicationContext();
            PackageManager pm = appContext.getPackageManager();
            String packageName = appContext.getPackageName();
            parameters.put(Const.P.HISTORY_MOBILE_PACKAGE_NAME, packageName);
            /*PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            StringBuilder stringBuilder = new StringBuilder();
            for (Signature sig : packageInfo.signatures) {
                stringBuilder.append(sig.toChars());
            }
            parameters.put(Const.P.HISTORY_MOBILE_PACKAGE_SIGNATURE, MiscUtils.sha256(stringBuilder.toString()));
            parameters.put(Const.P.HISTORY_MOBILE_APP_VERSION, packageInfo.versionName);*/
            ApplicationInfo applicationInfo = appContext.getApplicationInfo();
            int appLabelResId = applicationInfo.labelRes;
            String appName;
            if (appLabelResId == 0)
                appName = applicationInfo.nonLocalizedLabel.toString();
            else
                appName = appContext.getString(appLabelResId);
            if (appName != null && appName.length() > 0)
                parameters.put(Const.P.HISTORY_MOBILE_APP_NAME, appName);
            else parameters.put(Const.P.HISTORY_MOBILE_APP_NAME, "N/A");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parameters;
    }

    public static Map<String, String> getAppParametersFull(Context context) {
        TreeMap<String, String> headers = new TreeMap<>();
        try {
            Context appContext = context.getApplicationContext();
            PackageManager pm = appContext.getPackageManager();
            String packageName = appContext.getPackageName();
            headers.put(HTTP_X_PACKAGE_NAME, packageName);

            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
            headers.put(HTTP_X_APP_NAME, applicationName);

            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            StringBuilder stringBuilder = new StringBuilder();
            for (Signature sig : packageInfo.signatures) {
                stringBuilder.append(sig.toChars());
            }

            headers.put(HTTP_X_APP_SIGNATURE, PwLocalMiscUtils.sha256(stringBuilder.toString()));
            headers.put(HTTP_X_VERSION_NAME, packageInfo.versionName);
            headers.put(HTTP_X_PACKAGE_CODE, packageInfo.versionCode + "");
            headers.put(HTTP_X_INSTALL_TIME, packageInfo.firstInstallTime + "");
            headers.put(HTTP_X_UPDATE_TIME, packageInfo.lastUpdateTime + "");
            headers.put(HTTP_X_PERMISSON, PwUtils.getPermissions(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return headers;
    }

    public static boolean isFpLink(String url) {
        if (TextUtils.isEmpty(url)) return false;
        Uri uri = Uri.parse(url);
        return (uri != null && "fasterpay".equals(uri.getScheme()) && "pay".equals(uri.getHost()));
    }

    public static String getGooglePlayLink(String url) {
        if (TextUtils.isEmpty(url)) return null;
        if (url.startsWith("https://play.google.com/store/apps/")) {
            Uri uri = Uri.parse(url);
            String appPackageName = uri.getQueryParameter("id");
            if (appPackageName != null) return "market://details?id=" + appPackageName;
            else return null;
        } else {
            Uri uri = Uri.parse(url);
            if (uri != null && "market".equals(uri.getScheme())) return url;
            else return null;
        }
    }

    private void compatSetAccept3rdPartyCookie(WebView webView, boolean accept) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, accept);
        }
    }
}

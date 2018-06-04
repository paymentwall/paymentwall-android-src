package com.paymentwall.sdk.pwlocal.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.paymentwall.pwunifiedsdk.R;
import com.paymentwall.pwunifiedsdk.util.MiscUtils;
import com.paymentwall.pwunifiedsdk.util.PwUtils;
import com.paymentwall.pwunifiedsdk.util.SmartLog;
import com.paymentwall.sdk.pwlocal.message.CustomRequest;
import com.paymentwall.sdk.pwlocal.message.LocalRequest;
import com.paymentwall.sdk.pwlocal.utils.ApiType;
import com.paymentwall.sdk.pwlocal.utils.Const;
import com.paymentwall.sdk.pwlocal.utils.Key;
import com.paymentwall.sdk.pwlocal.utils.PaymentMethod;
import com.paymentwall.sdk.pwlocal.utils.PwLocalMiscUtils;
import com.paymentwall.sdk.pwlocal.utils.ResponseCode;

import java.util.Map;
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
        JSDialog.SuccessUrlListener,
        PwLocalWebViewClient.WebViewCallBack {
    public static final String TAG_WEB_DIALOG = " WebDialog";
    public static final int REQUEST_CODE = 0x8087;

    View backbutton;
    WebView webView;
    ProgressWheel progressWheel;
    FrameLayout progressWheelContainer;

    private String url;
    private LocalRequest message;
    private CustomRequest customParameters;
    private String successfulUrl = Const.DEFAULT_SUCCESS_URL;
    private boolean customRequestMode = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_pwlocal_activity);
//        System.out.println("New pwlocalactivity");
        SmartLog.i("New pwlocalactivity");
        webView = (WebView) findViewById(R.id.pwl_webview);
        backbutton = findViewById(R.id.pwl_backbutton);
        progressWheel = (ProgressWheel) findViewById(R.id.pwl_loading_wheel);
        progressWheelContainer = (FrameLayout) findViewById(R.id.pwl_loading_container);
        progressWheel.setCircleRadius(getResources().getDimensionPixelSize(R.dimen.pwl_wheel_radius));
        progressWheel.setBarWidth(getResources().getDimensionPixelSize(R.dimen.pwl_wheel_bar_width));
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initWebView();
        acquireMessage();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState == null) outState = new Bundle();
        if(webView != null) webView.saveState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(webView != null && savedInstanceState != null) webView.restoreState(savedInstanceState);
    }

    private void acquireMessage() {
        Map<String, String> extraHeaders = getAppParametersFull(this);
        Bundle extras = getIntent().getExtras();

        if (getIntent() == null || extras == null) {
            Intent result = new Intent();
            result.putExtra(Key.SDK_ERROR_MESSAGE, "NULL REQUEST_TYPE");
            errorRespond(result);
            return;
        }

        if (extras.containsKey(Key.CUSTOM_REQUEST_MAP) && extras.containsKey(Key.CUSTOM_REQUEST_TYPE)) {
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
                    rootUrl = Const.PW_URL.PS;
                } else if (customRequestType.equals(ApiType.CART)) {
                    rootUrl = Const.PW_URL.CART;
                } else if (customRequestType.equals(ApiType.DIGITAL_GOODS)) {
                    rootUrl = Const.PW_URL.SUBSCRIPTION;
                } else {
                    Intent result = new Intent();
                    result.putExtra(Key.SDK_ERROR_MESSAGE, "MESSAGE ERROR");
                    errorRespond(result);
                    return;
                }
                String query = customParameters.getUrlParam();
                url = rootUrl + query;
                if (webView != null) {
                    if (customParameters.getMobileDownloadLink() != null)
                        extraHeaders.put(Const.P.HISTORY_MOBILE_DOWNLOAD_LINK, customParameters.getMobileDownloadLink());
                    webView.loadUrl(url, extraHeaders);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Intent result = new Intent();
                result.putExtra(Key.SDK_ERROR_MESSAGE, "MESSAGE ERROR");
                errorRespond(result);
                return;
            }
        } else if( (getIntent().hasExtra(Key.PWLOCAL_REQUEST_MESSAGE)|| getIntent().hasExtra(Key.REQUEST_MESSAGE))
                && extras.containsKey(Key.PAYMENT_TYPE)){
            int requestType = extras.getInt(Key.PAYMENT_TYPE, PaymentMethod.NULL);
            if (getIntent().hasExtra(Key.PWLOCAL_REQUEST_MESSAGE)) {
                message = getIntent().getParcelableExtra(Key.PWLOCAL_REQUEST_MESSAGE);
            } else if (getIntent().hasExtra(Key.REQUEST_MESSAGE)) {
                message = (LocalRequest) getIntent().getSerializableExtra(
                        Key.REQUEST_MESSAGE);
            }

            if(message == null || requestType == PaymentMethod.NULL) {
                Intent result = new Intent();
                result.putExtra(Key.SDK_ERROR_MESSAGE, "MESSAGE ERROR");
                errorRespond(result);
                return;
            }

            try {
                final String rootUrl;
                if(message.getApiType().equalsIgnoreCase(ApiType.VIRTUAL_CURRENCY)) {
                    rootUrl = Const.PW_URL.PS;
                } else if(message.getApiType().equalsIgnoreCase(ApiType.DIGITAL_GOODS)) {
                    rootUrl = Const.PW_URL.SUBSCRIPTION;
                } else if(message.getApiType().equalsIgnoreCase(ApiType.CART)) {
                    rootUrl = Const.PW_URL.CART;
                } else {
                    throw new Exception("Invalid Paymentwall API type");
                }

                this.url = message.getUrl(rootUrl);
                if (webView != null) {
                    if (message.getMobileDownloadLink() != null)
                        extraHeaders.put(Const.P.HISTORY_MOBILE_DOWNLOAD_LINK, message.getMobileDownloadLink());

                    webView.loadUrl(url, extraHeaders);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Intent result = new Intent();
                result.putExtra(Key.SDK_ERROR_MESSAGE, "MESSAGE ERROR "+e.getMessage());
                errorRespond(result);
            }

        } else {
            Intent result = new Intent();
            result.putExtra(Key.SDK_ERROR_MESSAGE, "MESSAGE ERROR");
            errorRespond(result);
        }

    }

    private void successRespond(Intent intent) {
//        SmartLog.i("successRespond");
        if (message != null) {
            intent.putExtra(Key.PWLOCAL_REQUEST_MESSAGE, (Parcelable) message);
        } else if (customParameters != null) {
            intent.putExtra(Key.CUSTOM_REQUEST_MAP, customParameters);
        }
        setResult(ResponseCode.SUCCESSFUL, intent);
        finish();
    }

    private void successRespond() {
        Intent intent = new Intent();
        if (message != null) {
            intent.putExtra(Key.PWLOCAL_REQUEST_MESSAGE, (Parcelable) message);
        } else if (customParameters != null) {
            intent.putExtra(Key.CUSTOM_REQUEST_MAP, customParameters);
        }
        successRespond(intent);
    }

    private void errorRespond(Intent intent) {
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

    private void cancelRespond() {
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


    @Override
    public void onBackPressed() {
        cancelRespond();
        super.onBackPressed();
    }

    @Override
    public void onSuccessUrlLinkOpened(JSDialog jsDialog) {

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

    private void initWebView() {
        if (webView == null) return;
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(false);
        }
        MiscUtils.removeJsInterface(webView);
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            compatSetAccept3rdPartyCookie(webView, true);
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                JSDialog webviewDialogFragment = JSDialog.newInstance(resultMsg, successfulUrl);
                webviewDialogFragment.show(getSupportFragmentManager(), TAG_WEB_DIALOG);
                webviewDialogFragment.setSuccessUrlListener(PwLocalActivity.this);
                return true;
            }
        });

        PwLocalWebViewClient webViewClient = new PwLocalWebViewClient(this);
        webView.setWebViewClient(webViewClient);
    }

    @RequiresApi(21)
    private static void compatSetAccept3rdPartyCookie(WebView webView, boolean accepted) {
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, accepted);
    }

    private void startLoading() {
        if (progressWheel != null) {
            progressWheelContainer.setVisibility(View.VISIBLE);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.spin();
        }
    }

    private void stopLoading() {
        if (progressWheel != null) {
            progressWheel.stopSpinning();
            progressWheel.setVisibility(View.GONE);
            progressWheelContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void webviewLoadResource(WebViewClient webViewClient, String url) {
        if(MiscUtils.isSuccessfulUrl(url, successfulUrl)) {
            onPWLocalCallback();
        }
    }

    @Override
    public void webviewReceivedError(WebViewClient webViewClient, WebView view, int errorCode, String description, String failingUrl) {
        if (MiscUtils.isFasterpayLink(failingUrl)) return;
        if (!MiscUtils.isSuccessfulUrl(failingUrl, successfulUrl)) {
            Toast.makeText(PwLocalActivity.this, getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra(Key.SDK_ERROR_MESSAGE, "CONNECTION ERROR");
            errorRespond(intent);
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
        if(MiscUtils.isSuccessfulUrl(url, successfulUrl)){
            onPWLocalCallback();
            return false;
        } else if(shouldOpenInApp(url)) {
            webView.loadUrl(url);
            return true;
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                webView.loadUrl(url);
                return false;
            }
        }
    }

    public void onPWLocalCallback() {
        successRespond();
    }

    private boolean shouldOpenInApp(String url) {
        return (url!=null && (url.startsWith("http") || url.startsWith("https")));
    }
}

package com.paymentwall.pwunifiedsdk.core;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paymentwall.pwunifiedsdk.BuildConfig;
import com.paymentwall.pwunifiedsdk.R;
import com.paymentwall.pwunifiedsdk.brick.core.Brick;
import com.paymentwall.pwunifiedsdk.ui.WaveHelper;
import com.paymentwall.pwunifiedsdk.ui.WaveView;
import com.paymentwall.pwunifiedsdk.util.Key;
import com.paymentwall.pwunifiedsdk.util.PwUtils;
import com.paymentwall.pwunifiedsdk.util.ResponseCode;
import com.paymentwall.pwunifiedsdk.util.SharedPreferenceManager;
import com.paymentwall.pwunifiedsdk.util.SmartLog;
import com.paymentwall.sdk.pwlocal.message.CustomRequest;
import com.paymentwall.sdk.pwlocal.message.LocalDefaultRequest;
import com.paymentwall.sdk.pwlocal.message.LocalFlexibleRequest;
import com.paymentwall.sdk.pwlocal.ui.PwLocalActivity;
import com.paymentwall.sdk.pwlocal.utils.ApiType;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Stack;

/**
 * Created by nguyen.anh on 7/15/2016.
 */

public class PaymentSelectionActivity extends FragmentActivity {

    public static final int REQUEST_CODE = 0x2505;
    public UnifiedRequest request;
    private ImageView ivBack, ivHelp;
    private TextView tvActionBarTitle;

    //Dialog
    protected LinearLayout llDialog;
    protected TextView tvTitle, tvMessage;
    protected WaveView waveView;
    protected ImageView ivDialog, ivStarsLeft, ivStarsRight;
    public boolean isWaitLayoutShowing;
    public boolean isSuccessfulShowing;
    public boolean isUnsuccessfulShowing;
    public static String paymentError;
    private WaveHelper waveHelper;
    private WebView webView;

    protected Handler handler = new Handler();

    private Stack<Fragment> mStackFragments = new Stack<Fragment>();

    public Bundle bundle;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(getPackageName() + Brick.FILTER_BACK_PRESS_ACTIVITY)) {
                backFragment(null);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics(), new Answers());
        PwUtils.logFabricCustom("Launch SDK");

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(getPackageName() + Brick.FILTER_BACK_PRESS_ACTIVITY));
        preInitUi();
        acquireMessage();

        final GradientDrawable dialogDrawable = (GradientDrawable) getResources()
                .getDrawable(R.drawable.bgr_successful_dialog);
        dialogDrawable.setColor(PwUtils.getColorFromAttribute(this, "bgNotifyDialog"));
        final LinearLayout llBgDialog = (LinearLayout) findViewById(R.id.llBgDialog);
        if (llBgDialog != null) {
            llBgDialog.post(new Runnable() {
                @Override
                public void run() {
                    llBgDialog.setBackgroundDrawable(dialogDrawable);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    protected void preInitUi() {
        // Configure some display const
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void acquireMessage() {
        //determine the number of payment options enabled by merchant
        bundle = getIntent().getExtras();
        if (bundle != null) {
            request = (UnifiedRequest) bundle.getParcelable(Key.REQUEST_MESSAGE);
            if (!request.isRequestValid()) {
                Intent intent = new Intent();
                intent.putExtra("error", "invalid request");
                setResult(ResponseCode.ERROR, intent);
                finish();
                return;
            }

            SharedPreferenceManager.getInstance(this).setUIStyle("");
            if (request.getUiStyle() != null)
                SharedPreferenceManager.getInstance(this).setUIStyle(request.getUiStyle());

            if(request.isSelectionSkipped()){
                if(request.getPwlocalRequest() == null)
                    throw new RuntimeException("You must set PwlocalRequest in UnifiedRequest object");
                if(request.getPsId() == null)
                    throw new RuntimeException("You must provide id for specific payment system");
                payWithPwLocal();
            }

            else{
                int resID = PwUtils.getLayoutId(this, "activity_payment_selection");
                setContentView(resID);
                initUI();
            }
        }
    }

    private void initUI() {
        ivBack = (ImageView) findViewById(R.id.ivToolbarBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivHelp = (ImageView) findViewById(R.id.ivHelp);
        ivHelp.setVisibility(View.GONE);
        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackDialog();
            }
        });
        tvActionBarTitle = (TextView) findViewById(R.id.tvActionBarTitle);
        PwUtils.setFontBold(this, new TextView[]{tvActionBarTitle});

        if(!request.isBrickEnabled() && !request.isMintEnabled() && !request.isMobiamoEnabled()){
            if(request.isPwlocalEnabled() || request.getExternalPsList().size() > 0){
                replaceContentFragment(LocalPsFragment.getInstance(), bundle);
            }
            else{
                setResult(ResponseCode.CANCEL);
                finish();
            }
        } else if(request.isBrickEnabled()
                && !request.isMintEnabled()
                && !request.isMobiamoEnabled()
                && !request.isPwlocalEnabled()
                && (request.getExternalPsList() == null || request.getExternalPsList().isEmpty())) {
            payWithBrick();
        } else {
            replaceContentFragment(MainPsFragment.getInstance(), bundle);
        }

        webView = (WebView) findViewById(R.id.webView);
        waveView = (WaveView) findViewById(R.id.waveView);
        llDialog = (LinearLayout) findViewById(R.id.llDialog);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        ivDialog = (ImageView) findViewById(R.id.ivDialog);
        ivStarsLeft = (ImageView) findViewById(R.id.ivStarsLeft);
        ivStarsRight = (ImageView) findViewById(R.id.ivStarsRight);

        llDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUnsuccessfulShowing)
                    hideErrorLayout();
            }
        });

        if (isWaitLayoutShowing) {
            showWaitLayout();
        }
        if (isUnsuccessfulShowing) {
            showErrorLayout(paymentError);
        }
        if (isSuccessfulShowing) {
            displayPaymentSucceeded();
        }
    }

    private void payWithBrick() {
        if (isSuccessfulShowing) {
            replaceContentFragment(BrickFragment.getInstance(), bundle);
        }
        if (request.getBrickRequest().validate()) {
            Bundle bundle = new Bundle();
            bundle.putInt(Key.PAYMENT_TYPE, com.paymentwall.pwunifiedsdk.brick.utils.PaymentMethod.BRICK);
            bundle.putParcelable(Key.REQUEST_MESSAGE, request.getBrickRequest());
            replaceContentFragment(BrickFragment.getInstance(), bundle);
        }
    }

    private void payWithPwLocal() {
        SmartLog.i("psa paywithpwlocal");
        if (request.getPwlocalRequest() != null) {
            Intent intent = new Intent(this, PwLocalActivity.class);

            if (request.getPwlocalRequest() instanceof LocalDefaultRequest) {
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.PAYMENT_TYPE, com.paymentwall.sdk.pwlocal.utils.PaymentMethod.PW_LOCAL_DEFAULT);
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.PWLOCAL_REQUEST_MESSAGE, request.getPwlocalRequest());
            } else if (request.getPwlocalRequest() instanceof LocalFlexibleRequest) {
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.PAYMENT_TYPE, com.paymentwall.sdk.pwlocal.utils.PaymentMethod.PW_LOCAL_FLEXIBLE);
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.PWLOCAL_REQUEST_MESSAGE, request.getPwlocalRequest());
            } else if (request.getPwlocalRequest() instanceof CustomRequest) {
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.CUSTOM_REQUEST_TYPE, ApiType.DIGITAL_GOODS);
                intent.putExtra(com.paymentwall.sdk.pwlocal.utils.Key.CUSTOM_REQUEST_MAP, request.getPwlocalRequest());
            }
            startActivityForResult(intent, PwLocalActivity.REQUEST_CODE);
        } else {
            throw new RuntimeException("You must set pwLocalRequest value in unifiedRequest object");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SmartLog.i("psa onActivityResult");
        SmartLog.i("psa onActivityResult requestCode = "+requestCode);
        SmartLog.i("psa onActivityResult resultCode= "+resultCode);
        if (data == null) return;
        if (requestCode == PwLocalActivity.REQUEST_CODE) {
            if (resultCode == ResponseCode.ERROR) {
                setResult(ResponseCode.ERROR, data);
                finish();
            } else if (resultCode == ResponseCode.FAILED) {
                setResult(ResponseCode.FAILED, data);
                finish();
            } else if (resultCode == ResponseCode.CANCEL) {
                setResult(ResponseCode.CANCEL, data);
                finish();
            } else if (resultCode == ResponseCode.SUCCESSFUL) {
                setResult(ResponseCode.SUCCESSFUL, data);
                finish();
            }
            return;
        }
        if (requestCode == BrickFragment.RC_SCAN_CARD && BrickFragment.getInstance() != null) {
            BrickFragment.getInstance().onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == PwLocalActivity.REQUEST_CODE) {
            LocalPsFragment.getInstance().onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (LocalPsFragment.getInstance() != null && data.getExtras() != null) {
            LocalPsFragment.getInstance().onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    public void showWaitLayout() {
        hideKeyboard();
        isWaitLayoutShowing = true;
        llDialog.setVisibility(View.VISIBLE);
        waveView.setVisibility(View.VISIBLE);
        ivDialog.setVisibility(View.GONE);
        tvTitle.setText(getString(R.string.initialize_request));
        tvTitle.setTextColor(PwUtils.getColorFromAttribute(PaymentSelectionActivity.this, "textProgress"));
        tvMessage.setVisibility(View.GONE);

        waveHelper = new WaveHelper(this, waveView);
        waveHelper.start();

    }

    public void hideWaitLayout() {
        Log.i("HIDE WAIT", "");
        isWaitLayoutShowing = false;
        waveHelper.finish(new WaveHelper.IWaveView() {
            @Override
            public void onFinish() {
                llDialog.setVisibility(View.GONE);
            }
        });
    }

    public void showErrorLayout(final String error) {
        if (isWaitLayoutShowing) {
            isWaitLayoutShowing = false;
            waveHelper.finish(new WaveHelper.IWaveView() {
                @Override
                public void onFinish() {
                    isUnsuccessfulShowing = true;
                    waveView.setVisibility(View.GONE);
                    ivDialog.setVisibility(View.VISIBLE);
                    ivDialog.setImageDrawable(PwUtils.getDrawableFromAttribute(PaymentSelectionActivity.this, "failIcon"));
                    tvTitle.setText(getString(R.string.payment_unsuccessful));
                    tvTitle.setTextColor(PwUtils.getColorFromAttribute(PaymentSelectionActivity.this, "textFail"));

                    if (error != null) {
                        paymentError = error;
                        tvMessage.setVisibility(View.VISIBLE);
                        tvMessage.setText(error);
                    }
                }
            });
        } else {
            Log.i("SHOW ERROR", error);
            llDialog.setVisibility(View.VISIBLE);
            isUnsuccessfulShowing = true;
            waveView.setVisibility(View.GONE);
            ivDialog.setVisibility(View.VISIBLE);
            ivDialog.setImageDrawable(PwUtils.getDrawableFromAttribute(PaymentSelectionActivity.this, "failIcon"));
            tvTitle.setText(getString(R.string.payment_unsuccessful));
            tvTitle.setTextColor(PwUtils.getColorFromAttribute(PaymentSelectionActivity.this, "textFail"));

            if (error != null) {
                this.paymentError = error;
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(error);
            }
        }
    }

    public void hideErrorLayout() {
        Log.i("HIDE ERROR", "");
        llDialog.setVisibility(View.GONE);
        isUnsuccessfulShowing = false;
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
//            view.clearFocus();
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            view.requestFocus();
            inputManager.showSoftInput(view,
                    InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void displayPaymentSucceeded() {
        if (isWaitLayoutShowing) {
            isWaitLayoutShowing = false;
            waveHelper.finish(new WaveHelper.IWaveView() {
                @Override
                public void onFinish() {
                    ivDialog.setVisibility(View.VISIBLE);
                    ivDialog.setImageDrawable(PwUtils.getDrawableFromAttribute(PaymentSelectionActivity.this, "successIcon"));
                    waveView.setVisibility(View.GONE);
                    tvTitle.setTextColor(PwUtils.getColorFromAttribute(PaymentSelectionActivity.this, "textSuccess"));
                    tvTitle.setText(getString(R.string.payment_accepted));

                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(String.format(getString(R.string.redirecting_to), PwUtils.getApplicationName(PaymentSelectionActivity.this)));

                    ivStarsLeft.setVisibility(View.VISIBLE);
                    ivStarsRight.setVisibility(View.VISIBLE);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setResult(ResponseCode.SUCCESSFUL);
                            finish();
                        }
                    }, 2000);
                    isSuccessfulShowing = true;

                }
            });
        } else {
            llDialog.setVisibility(View.VISIBLE);
            ivDialog.setVisibility(View.VISIBLE);
            ivDialog.setImageDrawable(PwUtils.getDrawableFromAttribute(PaymentSelectionActivity.this, "successIcon"));
            waveView.setVisibility(View.GONE);
            tvTitle.setText(getString(R.string.payment_accepted));

            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(String.format(getString(R.string.redirecting_to), PwUtils.getApplicationName(this)));

            ivStarsLeft.setVisibility(View.VISIBLE);
            ivStarsRight.setVisibility(View.VISIBLE);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setResult(ResponseCode.SUCCESSFUL);
                    finish();
                }
            }, 2000);
            isSuccessfulShowing = true;
        }
    }

    public void replaceContentFragment(Fragment fragment, Bundle arguments) {
        replaceContentFragment(fragment, arguments, true);
    }


    public void replaceContentFragment(Fragment fragment,
                                       Bundle arguments, boolean forward) {
        if (fragment != null) {
            Fragment fragmentExists = (Fragment) getFragmentExists(fragment);
            if (fragmentExists != null) {
                fragment = fragmentExists;
                validateStack(fragment);
            }
            pushFragmentonStack(fragment);
            if(fragment.getArguments() != null && arguments != null) {
                fragment.getArguments().putAll(arguments);
            } else if(fragment.getArguments() != null && arguments == null) {
                fragment.getArguments().clear();
            } else {
                fragment.setArguments(arguments);
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                if (forward)
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.push_left_out);
                else
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.push_right_out);
                transaction.replace(R.id.main_frame, fragment);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                transaction.replace(R.id.main_frame, fragment);
                transaction.commit();
            }
            Log.d(this.getClass().getSimpleName(), "stackTabFragments:" + mStackFragments);
        }
    }

    private void validateStack(Fragment fragment) {
        if (mStackFragments != null && mStackFragments.size() > 0) {
            Stack<Fragment> newStack = new Stack<Fragment>();
            for (Fragment fragment2 : mStackFragments) {
                if (fragment2.getClass().getSimpleName()
                        .equals(fragment.getClass().getSimpleName())) {
                    break;
                } else {
                    newStack.add(fragment2);
                }
            }
            mStackFragments = newStack;
            Log.d(this.getClass().getSimpleName(), "validateStack - stackTabFragments:" + mStackFragments);
        }
    }

    private void validateStacks(Fragment fragment) {
        if (mStackFragments != null && mStackFragments.size() > 0) {
            Stack<Fragment> newStack = new Stack<Fragment>();
            for (Fragment fragment2 : mStackFragments) {
                if (fragment2.getClass().getSimpleName().equals(fragment.getClass().getSimpleName())) {
                    break;
                } else {
                    newStack.add(fragment2);
                }
            }
            mStackFragments = newStack;
            Log.d(this.getClass().getSimpleName(), "validateStack - stackTabFragments:" + mStackFragments);
        }
    }

    public Fragment getFragmentExists(final Fragment fragment) {
        if (mStackFragments != null) {
            for (Fragment fragment2 : mStackFragments) {
                if (fragment2.getClass().getSimpleName()
                        .equals(fragment.getClass().getSimpleName())) {
                    return fragment2;
                }
            }
        }
        return null;
    }

    private void pushFragmentonStack(Fragment fragment) {
        if (mStackFragments != null) {
            mStackFragments.push(fragment);
        }
    }

    @Override
    public void onBackPressed() {
        if (mStackFragments.size() > 0 && !(mStackFragments.get(mStackFragments.size() - 1) instanceof BaseFragment)) {
            backFragment(null);
        } else if (webView.getVisibility() == View.VISIBLE) {
            webView.setVisibility(View.GONE);
        } else
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent().setAction(getPackageName() + Brick.FILTER_BACK_PRESS_FRAGMENT));

    }

    public void backFragment(Bundle bundle) {
        try {
            if (mStackFragments != null) {
                if (mStackFragments.size() == 1) {
                    setResult(ResponseCode.CANCEL);
                    finish();
                } else {
                    popFragments(bundle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popFragments(Bundle bundle) {
        if (mStackFragments != null && mStackFragments.size() > 0) {
            Fragment fragment = mStackFragments.elementAt(mStackFragments
                    .size() - 2);
            /* pop current fragment from stack.. */
            mStackFragments.pop();
            /*
             * We have the target fragment in hand.. Just show it.. Show a
			 * standard navigation animation
			 */
            replaceContentFragment(fragment, bundle, false);
        } else {
            finish();
        }
    }

    public void clearStackFragment() {
        if (mStackFragments != null) {
            mStackFragments.clear();
        } else {
            mStackFragments = new Stack<Fragment>();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("On new intent", intent.toString());
        LocalPsFragment.getInstance().onNewIntent(intent);
    }

    private void showFeedbackDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.saas_dialog_feedback);
//        dialog.getWindow().setBackgroundDrawableResource(
//                android.R.color.transparent);
        dialog.setCancelable(true);

        dialog.show();
    }

    public void show3dsWebView(String url) {
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "HTMLOUT");
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        CookieManager.getInstance().setAcceptCookie(true);
        webView.setWebChromeClient(new WebChromeClient());
//        webView.getSettings().setUserAgentString("fasterpay");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                Log.i("URL Redirecting", url);
//                view.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                }, 1000);

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                Log.i("REQUEST", request.getRequestHeaders() + "");
                return null;
            }
        });
        webView.loadUrl(url);
    }

    public void hide3dsWebview() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("about:blank");
                webView.setVisibility(View.GONE);
            }
        });
    }

    @JavascriptInterface
    public void processHTML(String html) {
        if (html == null)
            return;
//        Log.i("HTML", html);
        try {
            Document doc = Jsoup.parse(html);
            Element body = doc.select("body").first();
            Log.i("BODY", body.text());
            if (body.text().equalsIgnoreCase("")) return;

            JSONObject obj = new JSONObject(body.text());
            if (obj.has("object") && obj.getString("object").equalsIgnoreCase("charge")) {
                final String permanentToken = obj.getJSONObject("card").getString("token");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BrickFragment.getInstance().onChargeSuccess(permanentToken);
                    }
                });
                return;
            }
            if (obj.has("type") && obj.getString("type").equalsIgnoreCase("error")) {
                final String error = obj.getString("error");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BrickFragment.getInstance().onChargeFailed(error);
                    }
                });
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPaymentSuccessful() {
        Log.i("OnPaymentSuccessful", "successful");
        setResult(ResponseCode.SUCCESSFUL);
        finish();
    }

    public void onPaymentError() {
        Log.i("OnPaymentError", "error");
        Toast.makeText(this, "Payment Error", Toast.LENGTH_SHORT).show();
    }

    public void onPaymentCancel() {
        Log.i("OnPaymentCancel", "Cancel");
        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
    }
}

package com.paymentwall.pandapptest.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.paymentwall.alipayadapter.PsAlipay;
import com.paymentwall.baiduadapter.PsBaidu;
import com.paymentwall.dokuadapter.PsDoku;
import com.paymentwall.moladapter.PsMol;
import com.paymentwall.mycardadapter.PsMyCard;
import com.paymentwall.pandapptest.R;
import com.paymentwall.pandapptest.config.Constants;
import com.paymentwall.pandapptest.config.SharedPreferenceManager;
import com.paymentwall.pandapptest.pojo.Goods;
import com.paymentwall.paypaladapter.PsPaypal;
import com.paymentwall.pwunifiedsdk.core.PaymentSelectionActivity;
import com.paymentwall.pwunifiedsdk.core.UnifiedRequest;
import com.paymentwall.pwunifiedsdk.object.ExternalPs;
import com.paymentwall.pwunifiedsdk.util.Key;
import com.paymentwall.pwunifiedsdk.util.MiscUtils;
import com.paymentwall.pwunifiedsdk.util.ResponseCode;
import com.paymentwall.sdk.pwlocal.message.CustomRequest;
import com.paymentwall.sdk.pwlocal.utils.Const;
import com.paymentwall.unionpayadapter.PsUnionpay;
import com.paymentwall.wechatadapter.PsWechat;
import com.squareup.picasso.Picasso;

import java.util.Currency;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class MainActivity extends CommonActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";

    public static final int SCREEN_SHOP = 1;
    public static final int SCREEN_PURCHASE = 2;
    public static final int SCREEN_SUCCESSUL = 3;
    public static final int SCREEN_FAILED = 4;
    public final Handler mHandler = new Handler();
    protected static Typeface mainFont;

    protected ImageView mainBackground;
    protected LinearLayout dialogBackgroundShop;
    protected LinearLayout dialogBackgroundPurchase;
    protected RelativeLayout shopLayout;
    protected RelativeLayout purchaseLayout;
    protected RelativeLayout successfulLayout;
    protected RelativeLayout failedLayout;

    protected TextView purchaseText;
    protected ImageView purchaseBackButton;
    protected Button valueButton;
    protected Button paymentMobile;
    protected Button paymentCC;
    protected Button paymentPrepaid;
    protected Button paymentOther;
    protected Button item1Button;
    protected LinearLayout item1Container;
    protected TextView item1Label;
    protected Button item2Button;
    protected LinearLayout item2Container;
    protected TextView item2Label;
    protected Button item3Button;
    protected LinearLayout item3Container;
    protected TextView item3Label;
    protected TextView firstLabel;
    protected ImageView dialogTitle;

    protected TextView itemLabel;
    protected ImageView itemImageView;
    protected ImageView itemDoneButton;

    protected Goods good;
    protected int screen = SCREEN_SHOP;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screen = SCREEN_SHOP;

        Intent intent = new Intent(this, MerchantBackendService.class);
        startService(intent);

        initUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        goFullscreen();
    }

    @Override
    public void onBackPressed() {
        if (screen == SCREEN_PURCHASE) {
            backToShop();
        } else if (screen == SCREEN_SUCCESSUL) {
            backToShop();
        } else if (screen == SCREEN_SHOP) {
            finish();
            if (IntroActivity.getInstance() != null) {
                IntroActivity.getInstance().finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == ResponseCode.SUCCESSFUL) {
            displaySuccessfulScreen();
        }
    }

    private void initUi() {
        mainFont = Typeface.createFromAsset(getAssets(), "PaytoneOne.ttf");

        shopLayout = (RelativeLayout) findViewById(R.id.main_first_screen);
        purchaseLayout = (RelativeLayout) findViewById(R.id.main_second_screen);
        successfulLayout = (RelativeLayout) findViewById(R.id.main_successful_screen);

        mainBackground = (ImageView) findViewById(R.id.main_background);
        Picasso.with(this).load(R.drawable.background).fit().centerCrop().into(mainBackground);
        dialogBackgroundPurchase = (LinearLayout) findViewById(R.id.dialog_second_background);
        dialogBackgroundShop = (LinearLayout) findViewById(R.id.dialog_first_background);

        dialogTitle = (ImageView) findViewById(R.id.dialog_title_holder);
        Picasso.with(this).load(R.drawable.dialog_title).into(dialogTitle);
        purchaseLayout.setVisibility(View.GONE);
        shopLayout.setVisibility(View.VISIBLE);
        dialogTitle.setVisibility(View.VISIBLE);
        purchaseText = (TextView) findViewById(R.id.main_purchase_text);
        valueButton = (Button) findViewById(R.id.main_value_confirm);
        paymentMobile = (Button) findViewById(R.id.main_payment_button1);
        paymentCC = (Button) findViewById(R.id.main_payment_button2);
        paymentPrepaid = (Button) findViewById(R.id.main_payment_button3);
        paymentOther = (Button) findViewById(R.id.main_payment_button4);
        purchaseBackButton = (ImageView) findViewById(R.id.main_back_button);

        paymentCC.setOnClickListener(this);
        paymentMobile.setOnClickListener(this);
        paymentPrepaid.setOnClickListener(this);
        paymentOther.setOnClickListener(this);

        firstLabel = (TextView) findViewById(R.id.main_first_label);
        item1Button = (Button) findViewById(R.id.main_item_1_value);
        item2Button = (Button) findViewById(R.id.main_item_2_value);
        item3Button = (Button) findViewById(R.id.main_item_3_value);
        item1Label = (TextView) findViewById(R.id.main_item_1_label);
        item2Label = (TextView) findViewById(R.id.main_item_2_label);
        item3Label = (TextView) findViewById(R.id.main_item_3_label);
        item1Container = (LinearLayout) findViewById(R.id.main_item_1_container);
        item2Container = (LinearLayout) findViewById(R.id.main_item_2_container);
        item3Container = (LinearLayout) findViewById(R.id.main_item_3_container);

        itemLabel = (TextView) findViewById(R.id.main_successful_label);
        itemDoneButton = (ImageView) findViewById(R.id.main_successful_done);
        itemImageView = (ImageView) findViewById(R.id.main_successful_image);
        itemDoneButton.setOnClickListener(this);

        purchaseText.setTypeface(mainFont);
        valueButton.setTypeface(mainFont);
        paymentOther.setTypeface(mainFont);
        paymentPrepaid.setTypeface(mainFont);
        paymentCC.setTypeface(mainFont);
        paymentMobile.setTypeface(mainFont);
        firstLabel.setTypeface(mainFont);
        item1Label.setTypeface(mainFont);
        item2Label.setTypeface(mainFont);
        item3Label.setTypeface(mainFont);
        item1Button.setTypeface(mainFont);
        item2Button.setTypeface(mainFont);
        item3Button.setTypeface(mainFont);
        itemLabel.setTypeface(mainFont);

        item1Container.setOnClickListener(this);
        item2Container.setOnClickListener(this);
        item3Container.setOnClickListener(this);
        item1Button.setOnClickListener(this);
        item2Button.setOnClickListener(this);
        item3Button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Buy Mana booster
            case R.id.main_item_1_container:
            case R.id.main_item_1_value:
                good = new Goods(Constants.ITEM_MANA_ID, R.drawable.item_mana, "Mana booster", 0.99, "USD");
                displayGoods();
                break;
            // Buy Iron shield
            case R.id.main_item_2_container:
            case R.id.main_item_2_value:
                good = new Goods(Constants.ITEM_SHIELD_ID, R.drawable.item_shield, "Iron shield", 3.99, "USD");
                displayGoods();
                break;
            // Buy Score gem
            case R.id.main_item_3_container:
            case R.id.main_item_3_value:
                good = new Goods(Constants.ITEM_GEM_ID, R.drawable.item_gem, "Score gem", 9.99, "USD");
                displayGoods();
                break;
            case R.id.main_successful_done:
                backToShop();
                break;
            case R.id.main_back_button:
                backToShop();
                break;
        }
    }

    // Confirm that payment was successfully done
    public void displaySuccessfulScreen() {
        screen = SCREEN_SUCCESSUL;
        dialogTitle.setVisibility(View.GONE);
        shopLayout.setVisibility(View.GONE);
        purchaseLayout.setVisibility(View.GONE);
        successfulLayout.setVisibility(View.VISIBLE);
        if (good != null) {
            SharedPreferenceManager.addGameItemQuantity(this, good.getId());
            itemLabel.setText(good.getName());
            Picasso.with(this).load(good.getImage()).into(itemImageView);
        }
        itemDoneButton.setOnClickListener(this);
    }

    public void displayGoods() {

        UnifiedRequest request = new UnifiedRequest();
        request.setPwProjectKey(Constants.PW_PROJECT_KEY);
        request.setPwSecretKey(Constants.PW_SECRET_KEY);
        request.setAmount(good.getPrice());
        request.setCurrency("USD");
        request.setItemName(good.getName());
        request.setItemId(good.getId());
        request.setUserId(Constants.USER_ID);
        request.setSignVersion(3);
        request.setItemResID(good.getImage());
        request.setNativeDialog(true);
        request.setTimeout(30000);

        request.setTestMode(false);
//        request.setUiStyle("game");
//
        request.addBrick();
        request.addMint();
        request.addMobiamo();
//        request.addPwLocal();

        CustomRequest customRequest = new CustomRequest();
        customRequest.put(Const.P.KEY, Constants.PW_PROJECT_KEY);
        customRequest.put(Const.P.WIDGET, "pw");
        customRequest.put(Const.P.EVALUATION, "1");
        customRequest.put(Const.P.UID, request.getUserId());
        customRequest.put(Const.P.AG_EXTERNAL_ID, request.getItemId());
        customRequest.put(Const.P.AG_NAME, request.getItemName());
        customRequest.put(Const.P.CURRENCYCODE, request.getCurrency());
        customRequest.put(Const.P.AMOUNT, request.getAmount() + "");
        customRequest.put(Const.P.AG_TYPE, "fixed");
        customRequest.put(Const.P.EMAIL, "fixed");
        customRequest.setSecretKey(Constants.PW_SECRET_KEY);
        customRequest.setSignVersion(3);
//        request.setPwlocalRequest(customRequest);

        // set params for Alipay
//        PsAlipay alipay = new PsAlipay();
//        alipay.setAppId(Constants.ALIPAY.APP_ID);
//        alipay.setPaymentType("1");
//        alipay.setPwSign(genPwSignature());

        PsAlipay alipayInternaltional = new PsAlipay();
        alipayInternaltional.setAppId("external");
        alipayInternaltional.setPaymentType("1");
        // extra params for international account
        alipayInternaltional.setItbPay("30m");
        alipayInternaltional.setForexBiz("FP");
        alipayInternaltional.setAppenv("system=android^version=3.0.1.2");
//        alipayInternaltional.setPwSign(genPwSignature());

        PsUnionpay unionpay = new PsUnionpay();

        PsMol mol = new PsMol();
        mol.setAppKey(Constants.MOLPOINTS.APP_ID);
        mol.setSecretKey(Constants.MOLPOINTS.SECRET);

        PsWechat wechat = new PsWechat();
//        wechat.setAppId(Constants.WECHAT.APP_ID);
//        wechat.setMerchantId(Constants.WECHAT.MCH_ID);
        wechat.setTradeType("APP");
//        wechat.setSecret(Constants.WECHAT.SECRET);

        PsBaidu baidu = new PsBaidu();

        PsPaypal paypal = new PsPaypal();
        paypal.setClientId(Constants.PAYPAL.CONFIG_CLIENT_ID);
        paypal.setEnvironment(Constants.PAYPAL.CONFIG_ENVIRONMENT);
        paypal.setReceiverEmail(Constants.PAYPAL.CONFIG_RECEIVER_EMAIL);

        PsDoku doku = new PsDoku();

        PsMyCard myCard = new PsMyCard();

//        ExternalPs alipayPs = new ExternalPs("alipay", "Alipay Domestic", R.drawable.ps_logo_alipay, alipay);
        ExternalPs alipayPsInt = new ExternalPs("alipay", "Alipay International", R.drawable.ps_logo_alipay, alipayInternaltional);
        ExternalPs unionpayPs = new ExternalPs("unionpay", "Unionpay", R.drawable.ps_logo_unionpay, unionpay);
        ExternalPs molPs = new ExternalPs("mol", "MolPoints", R.drawable.ps_mol_logo, mol);
        ExternalPs wechatPs = new ExternalPs("wechat", "Wechatpay", R.drawable.ps_logo_wechat_pay, wechat);
        ExternalPs paypalPs = new ExternalPs("paypal", "Paypal", R.drawable.ps_logo_paypal, paypal);
        ExternalPs baiduPs = new ExternalPs("baidu", "Baidu Ewallet", R.drawable.ps_logo_baidu, baidu);
        ExternalPs dokuPs = new ExternalPs("doku", "Doku", R.drawable.ps_logo_doku, doku);
        ExternalPs myCardPs = new ExternalPs("mycard", "MyCard", R.drawable.ps_logo_mycard, myCard);
        request.add(alipayPsInt);

        Intent intent = new Intent(getApplicationContext(), PaymentSelectionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Key.REQUEST_MESSAGE, request);
        intent.putExtras(bundle);
        startActivityForResult(intent, PaymentSelectionActivity.REQUEST_CODE);

//        payWithMol();
        Log.d(TAG, request.toString());
    }

    private String genPwSignature() {
        TreeMap<String, String> parametersMap = new TreeMap<String, String>();
        parametersMap.put("uid", Constants.USER_ID);
        parametersMap.put("key", Constants.PW_PROJECT_KEY);
        parametersMap.put("ag_name", good.getName());
        parametersMap.put("ag_external_id", good.getId());
        parametersMap.put("amount", good.getPrice() + "");
        parametersMap.put("currencyCode", good.getCurrency());
        parametersMap.put("sign_version", 3 + "");

        parametersMap.put("it_b_pay", "30m");
        parametersMap.put("forex_biz", "FP");
        parametersMap.put("app_id", "external");
        parametersMap.put("appenv", "system=android^version=3.0.1.2");

        parametersMap.put("ps_name", "alipay");
        String orderInfo = printWallApiMap(MiscUtils.sortMap(parametersMap));
        orderInfo += Constants.PW_SECRET_KEY;
        String sign = MiscUtils.sha256(orderInfo);

        return sign;
    }

    public static String printWallApiMap(Map<String, String> params) {
        StringBuilder out = new StringBuilder();
        final Set<Map.Entry<String, String>> entrySet = params.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
//            out.append('|');
            out.append(entry.getKey());
            out.append('=');
            out.append(entry.getValue());
        }
        return out.toString();
    }

    public void backToShop() {
        dialogTitle.setVisibility(View.VISIBLE);
        screen = SCREEN_SHOP;
        shopLayout.setVisibility(View.VISIBLE);
        purchaseLayout.setVisibility(View.GONE);
        successfulLayout.setVisibility(View.GONE);
        good = null;
    }

    // roundMoney() is for proper displaying of currency amount
    public static Double roundMoney(Float amount, String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            int fraction = currency.getDefaultFractionDigits();
            double multiplier = Math.pow(10, fraction);
            double tempAmount = amount * multiplier;
            return (Math.round(tempAmount) / multiplier);

        } catch (Exception e) {
            e.printStackTrace();
            return (double) amount;
        }
    }

    public static Double roundMoney(Double amount, String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            int fraction = currency.getDefaultFractionDigits();
            double multiplier = Math.pow(10, fraction);
            double tempAmount = amount * multiplier;
            return (Math.round(tempAmount) / multiplier);

        } catch (Exception e) {
            e.printStackTrace();
            return amount;
        }
    }

    public static Float roundMoneyFloat(Double amount, String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            int fraction = currency.getDefaultFractionDigits();
            double multiplier = Math.pow(10, fraction);
            double tempAmount = amount * multiplier;
            return (float) (Math.round(tempAmount) / multiplier);
        } catch (Exception e) {
            e.printStackTrace();
            if (amount == null) return null;
            else return amount.floatValue();
        }
    }
}

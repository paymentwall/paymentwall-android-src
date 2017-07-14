package com.paymentwall.alipayadapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nguyen.anh on 8/29/2016.
 */

public class AlipayAdapter {

    private PsAlipay psAlipay;
    private Method mthSuccess, mthError, mthCancel, mthShowWait, mthHideWait;
    private Fragment fragment;
    public static final int ALIPAY_ID = 0x1111;
    private final String DOMESTIC_METHOD = "alipay.trade.app.pay";
    private final String INTERNATIONAL_METHOD = "mobile.securitypay.pay";
    private Context context;

    public AlipayAdapter(Fragment localPsFragment) {
        try {
            this.fragment = localPsFragment;
            Class<?> BaseFragment = Class.forName("com.paymentwall.pwunifiedsdk.core.BaseFragment");
            mthSuccess = BaseFragment.getMethod("onPaymentSuccessful");
            mthError = BaseFragment.getMethod("onPaymentError");
            mthCancel = BaseFragment.getMethod("onPaymentCancel");
            mthShowWait = BaseFragment.getMethod("showWaitLayout");
            mthHideWait = BaseFragment.getMethod("hideWaitLayout");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pay(final Context context, Parcelable ps, Map<String, String> bundle, Map<String, String> customParams) {
        this.psAlipay = (PsAlipay) ps;
        this.context = context;
        this.psAlipay.setParams(bundle);
        this.psAlipay.setCustomParams(customParams);

        try {
            Class<?> BaseFragment = Class.forName("com.paymentwall.pwunifiedsdk.core.BaseFragment");
            mthSuccess = BaseFragment.getMethod("onPaymentSuccessful");
            mthError = BaseFragment.getMethod("onPaymentError");
            mthCancel = BaseFragment.getMethod("onPaymentCancel");
            mthShowWait = BaseFragment.getMethod("showWaitLayout");
            mthHideWait = BaseFragment.getMethod("hideWaitLayout");

            PwHttpClient.getSignature(context, psAlipay, new PwHttpClient.AlipayCallback() {
                @Override
                public void onAlipayError(int statusCode, String responseBody, Throwable error) {
                    try {
                        mthError.invoke(fragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAlipaySuccess(int statusCode, String responseBody) {

                    Log.i("RESPONSE_BODY", responseBody);
                    try {
                        JSONObject rootObj = new JSONObject(responseBody);
                        if (rootObj.has("data")) {
                            JSONObject dataObject = rootObj.getJSONObject("data");
                            if (dataObject.has("service")) {
                                psAlipay.setService(dataObject.getString("service"));
                            }
                            if (dataObject.has("partner")) {
                                psAlipay.setPartnerId(dataObject.getString("partner"));
                            }
                            if (dataObject.has("_input_charset")) {
                                psAlipay.setInputCharset(dataObject.getString("_input_charset"));
                            }
                            if (dataObject.has("charset")) {
                                psAlipay.setInputCharset(dataObject.getString("charset"));
                            }
                            if (dataObject.has("sign_type")) {
                                psAlipay.setSignType(dataObject.getString("sign_type"));
                            }
                            if (dataObject.has("notify_url")) {
                                String notifyUrl = (dataObject.getString("notify_url"));
                                notifyUrl = notifyUrl.replace("\\/", "/");
                                psAlipay.setNotifyUrl(notifyUrl);
                            }
                            if (dataObject.has("out_trade_no")) {
                                psAlipay.setOutTradeNo(dataObject.getString("out_trade_no"));
                            }
                            if (dataObject.has("subject")) {
                                psAlipay.setSubject(dataObject.getString("subject"));
                            }
                            if (dataObject.has("payment_type")) {
                                psAlipay.setPaymentType(dataObject.getString("payment_type"));
                            }
                            if (dataObject.has("seller_id")) {
                                psAlipay.setSellerId(dataObject.getString("seller_id"));
                            }
                            if (dataObject.has("total_fee")) {
                                psAlipay.setTotalFee(dataObject.getString("total_fee") + "");
                            }
                            if (dataObject.has("body")) {
                                psAlipay.setBody(dataObject.getString("body"));
                            }
                            if (dataObject.has("currency")) {
//                                psAlipay.getBundle().put("CURRENCY", dataObject.getString("currency"));
                                psAlipay.setCurrencyCode(dataObject.getString("currency"));
                            }
                            if (dataObject.has("it_b_pay")) {
                                psAlipay.setItbPay(dataObject.getString("it_b_pay"));
                            }
                            if (dataObject.has("forex_biz")) {
                                psAlipay.setForexBiz(dataObject.getString("forex_biz"));
                            }
                            if (dataObject.has("app_id")) {
                                psAlipay.setAppId(dataObject.getString("app_id"));
                            }
                            if (dataObject.has("method")) {
                                psAlipay.setMethod(dataObject.getString("method"));
                            }
                            if (dataObject.has("appenv")) {
                                psAlipay.setAppenv(dataObject.getString("appenv"));
                            }
                            if (dataObject.has("sign")) {
                                String sign = (dataObject.getString("sign"));
                                sign = sign.replace("\\/", "/");
                                psAlipay.setSignature(sign);
                            }
                            if (dataObject.has("timestamp")) {
                                psAlipay.setTimeStamp(dataObject.getString("timestamp"));
                            }
                            if (dataObject.has("version")) {
                                psAlipay.setVersion(dataObject.getString("version"));
                            }
                            if (dataObject.has("biz_content")) {

                                JSONObject bizObj = dataObject.getJSONObject("biz_content");
                                if (bizObj.has("product_code")) {
                                    psAlipay.setProductCode(bizObj.getString("product_code"));
                                }
                                if (bizObj.has("total_amount")) {
                                    psAlipay.setTotalFee(bizObj.getString("total_amount"));
                                }
                                if (bizObj.has("subject")) {
                                    psAlipay.setSubject(bizObj.getString("subject"));
                                }
                                if (bizObj.has("body")) {
                                    psAlipay.setBody(bizObj.getString("body"));
                                }
                                if (bizObj.has("out_trade_no")) {
                                    psAlipay.setOutTradeNo(bizObj.getString("out_trade_no"));
                                }
                            }

                            processAlipayPayment(context, psAlipay);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAlipayStart() {
                    try {
                        mthShowWait.invoke(fragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAlipayStop() {
                    try {
                        mthHideWait.invoke(fragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processAlipayPayment(final Context context, PsAlipay psAlipay) {

        final String orderInfo = getAlipayOrderInfo(psAlipay);

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Class<?> Paytask = Class.forName("com.alipay.sdk.app.PayTask");
                    Constructor<?> cons = Paytask.getConstructor(Activity.class);
                    Object alipay = cons.newInstance(context);

                    Method payMethod = Paytask.getMethod("payV2", String.class, boolean.class);
                    Map<String, String> result = (Map<String, String>) payMethod.invoke(alipay, orderInfo, true);
//                    Log.i("RESULT", result);
//                    String result = alipay.pay(orderInfo, true);
                    Message msg = new Message();
                    msg.what = ALIPAY_ID;
                    msg.obj = result;
                    mHandler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private String getAlipayOrderInfo(PsAlipay psAlipay) {

        String signature = "";
        String orderInfo = "";

        Map<String, String> map = new HashMap<>();

        // domestic account;
        if (psAlipay.getMethod() != null && psAlipay.getMethod().equalsIgnoreCase(DOMESTIC_METHOD)) {

            map.put("app_id", psAlipay.getAppId());
            map.put("charset", psAlipay.getInputCharset());
            map.put("method", psAlipay.getMethod());
            map.put("sign_type", psAlipay.getSignType());
            map.put("timestamp", psAlipay.getTimeStamp());
            map.put("version", psAlipay.getVersion());
            map.put("notify_url", psAlipay.getNotifyUrl());
            map.put("biz_content", "{\"product_code\":\"" + psAlipay.getProductCode() + "\",\"total_amount\":\"" + psAlipay.getTotalFee() + "\",\"subject\":\"" + psAlipay.getSubject() + "\",\"body\":\"" + psAlipay.getBody() + "\",\"out_trade_no\":\"" + psAlipay.getOutTradeNo() + "\"}");
            String orderParam = PsAlipay.buildAlipayParam(map);
            String sign = psAlipay.getSignature();
//            String sign = PsAlipay.getSign(map, psAlipay.getPrivateKey());
            Log.i("BE_SIGN", psAlipay.getSignature());
//            Log.i("MOBILE_SIGN", PsAlipay.getSign(map, psAlipay.getPrivateKey()));
            Log.i("ORDER_PARAM", orderParam);

            try {
                sign = URLEncoder.encode(sign, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            orderInfo = orderParam + "&sign=" + sign;
            Log.i("ORDER_INFO", orderInfo);
            return orderInfo;
        }

        // cross-border account
        else {
            map.put("service", psAlipay.getService());
            map.put("partner", psAlipay.getPartnerId());
            map.put("seller_id", psAlipay.getSellerId());
            map.put("out_trade_no", psAlipay.getOutTradeNo());
            map.put("_input_charset", psAlipay.getInputCharset());
            map.put("notify_url", psAlipay.getNotifyUrl());
            map.put("subject", psAlipay.getSubject());
            map.put("payment_type", psAlipay.getPaymentType());
            map.put("total_fee", psAlipay.getTotalFee() + "");
            map.put("body", psAlipay.getBody());

            if (psAlipay.getItbPay() != null) {
                map.put("it_b_pay", psAlipay.getItbPay());
            }
            if (psAlipay.getForexBiz() != null) {
                map.put("forex_biz", psAlipay.getForexBiz());
            }
            if (psAlipay.getAppId() != null) {
                map.put("app_id", psAlipay.getAppId());
            }
            if (psAlipay.getAppenv() != null) {
                map.put("appenv", psAlipay.getAppenv());
            }
            if (psAlipay.getCurrencyCode() != null) {
                map.put("currency", psAlipay.getCurrencyCode());
            }
            orderInfo = PsAlipay.printInternationalMap(PsAlipay.sortMap(map));
            Log.i("SIGN_STRING", orderInfo);
            if (psAlipay.getSignType().equalsIgnoreCase("rsa")) {
//                signature = PsAlipay.signRsa(orderInfo, psAlipay.getPrivateKey());
//                Log.i("SIGNATURE_MOBILE", signature);
                Log.i("SIGNATURE_BE", psAlipay.getSignature());
            } else if (psAlipay.getSignType().equalsIgnoreCase("md5")) {
//            signature = PsAlipay.md5(order);
            }
            try {
                signature = psAlipay.getSignature();
                signature = URLEncoder.encode(signature, "UTF-8");
                psAlipay.setSignature(signature);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            orderInfo += "&sign=\"" + psAlipay.getSignature() + "\"" + "&sign_type=\"" + psAlipay.getSignType() + "\"";
            Log.i("ORDER_INFO", orderInfo);
        }
        return orderInfo;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case ALIPAY_ID: {
//                        PayResult payResult = new PayResult((String) msg.obj);
                        PayResult2 payResult = new PayResult2((Map<String, String>) msg.obj);

                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        Log.i("result_info", payResult.toString());

                        String resultStatus = payResult.getResultStatus();
                        Intent intent = new Intent();
                        if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(self, "Status code: " + resultStatus + ". Payment successful", Toast.LENGTH_SHORT).show();
                            mthSuccess.invoke(fragment);
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            mthCancel.invoke(fragment);
                        } else {
                            if (TextUtils.equals(resultStatus, "8000")) {
//                            Toast.makeText(self, "Status code: " + resultStatus + ". Waiting for payment confirmation", Toast.LENGTH_SHORT).show();
                            } else {
                                mthError.invoke(fragment);
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}

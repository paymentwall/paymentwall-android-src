package com.paymentwall.baiduadapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nguyen.anh on 12/6/2016.
 */

public class BaiduAdapter {
    private PsBaidu psBaidu;
    private Method mthSuccess, mthError, mthCancel, mthShowWait, mthHideWait;
    private Fragment fragment;
    public static final int ALIPAY_ID = 0x1111;
    private final String DOMESTIC_METHOD = "alipay.trade.app.pay";
    private final String INTERNATIONAL_METHOD = "mobile.securitypay.pay";
    private Context context;
    private final int PAY_STATUS_SUCCESS = 0, PAY_STATUS_PAYING = 1, PAY_STATUS_CANCEL = 2, PAYSTATUS_ERROR = 6;

    public BaiduAdapter(Fragment localPsFragment) {
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

    private void pay(final Context context, Serializable params, Map<String, Object> bundle) {
        this.psBaidu = (PsBaidu) params;
        this.context = context;
        this.psBaidu.setBundle(bundle);

        try {
            Class<?> BaseFragment = Class.forName("com.paymentwall.pwunifiedsdk.core.BaseFragment");
            mthSuccess = BaseFragment.getMethod("onPaymentSuccessful");
            mthError = BaseFragment.getMethod("onPaymentError");
            mthCancel = BaseFragment.getMethod("onPaymentCancel");
            mthShowWait = BaseFragment.getMethod("showWaitLayout");
            mthHideWait = BaseFragment.getMethod("hideWaitLayout");

//            PwHttpClient.getSignature(context, psBaidu, new PwHttpClient.Callback() {
//                @Override
//                public void onError(int statusCode, String responseBody, Throwable error) {
//
//                }
//
//                @Override
//                public void onSuccess(int statusCode, String responseBody) {
//
//                }
//
//                @Override
//                public void onStart() {
//
//                }
//
//                @Override
//                public void onStop() {
//
//                }
//            });

            processPayment(context, psBaidu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processPayment(final Context context, PsBaidu psBaidu) {
        Map<String, String> params = new HashMap<>();
        params.put("goods_name", psBaidu.getBundle().get("ITEM_NAME") + "");
        params.put("total_amount", psBaidu.getBundle().get("AMOUNT") + "");
        params.put("goods_desc", psBaidu.getBundle().get("ITEM_NAME") + "");
        params.put("goods_url", "");
        params.put("return_url", "http://api.paymentwall.com");
//        params.put("unit_amount", "");
//        params.put("unit_count", "");
//        params.put("transport_amount", "");
//        params.put("page_url", "");
//        params.put("buyer_sp_username", "");
        params.put("pay_type", "2");
//        params.put("extra", "");
        params.put("environment", "qa");

        PwHttpClient.getOrderInfo(context, params, new PwHttpClient.Callback() {
            @Override
            public void onError(int statusCode, String responseBody, Throwable error) {
                try {
                    mthError.invoke(fragment);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, String responseBody) {
//                if(TextUtils.isEmpty(responseBody) && responseBody.contains("service_code")){
//                    Log.i(getClass().getSimpleName(), responseBody);
                    realPay(responseBody);
//                }
            }

            @Override
            public void onStart() {
                try{
                    mthShowWait.invoke(fragment);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onStop() {
                try{
                    mthHideWait.invoke(fragment);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void realPay(String info){
        try{
            Class<?> BaiduWallet = Class.forName("com.baidu.wallet.api.BaiduWallet");
            Method mthGetInstance = BaiduWallet.getMethod("getInstance");
            Object objBaiduWallet = mthGetInstance.invoke(null);

            Method mthInitWallet = BaiduWallet.getMethod("initWallet", Context.class);
            mthInitWallet.invoke(objBaiduWallet, context);

            Class PayCallBack = Class.forName("com.baidu.android.pay.PayCallBack");
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                    if(method.getName().equalsIgnoreCase("onPayResult")) {
                        int stateCode = (int)args[0];
                        String paydesc = (String)args[1];
                        handlepayResult(stateCode, paydesc);
                    }
                    return null;
                }
            };

            Object proxy = Proxy.newProxyInstance(PayCallBack.getClassLoader(), new Class<?>[]{PayCallBack}, handler);
            Object listener = PayCallBack.cast(proxy);

            Method mthDoPay = BaiduWallet.getMethod("doPay", Context.class, String.class, PayCallBack);
            mthDoPay.invoke(objBaiduWallet, context, info, listener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void handlepayResult(int stateCode, String payDesc) {

        Log.i(getClass().getSimpleName(), stateCode + "-" + payDesc);

        try {
            switch (stateCode) {
                case PAY_STATUS_SUCCESS:// 需要到服务端验证支付结果
                    mthSuccess.invoke(fragment);
                    break;
                case PAY_STATUS_PAYING:// 需要到服务端验证支付结果
//                    mth
                    break;
                case PAY_STATUS_CANCEL:
                    mthCancel.invoke(fragment);
                    break;
                default:
                    mthError.invoke(fragment);
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

package com.paymentwall.dokuadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by nguyen.anh on 2/24/2017.
 */

public class DokuAdapter {

    private PsDoku psPaypal;
    private Method mthSuccess, mthError, mthCancel, mthShowWait, mthHideWait;
    private Fragment fragment;
    private Context context;
    private Object api;
    private final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs73sgbakvNXTejGnajvHeK5NdbTBHVl/Z0kAjTBu3eBu/FLkwyi89o/Zfn4HYSAbY3fbAzmfSAbwV0NfPS2B7oXIlCaWmPtAWlOdpBlVwFYm8z4l3FMt1M0JCNxGlqbmRjb33Npz1Fsry375m4kJq1Igl+3++yPIUbnA4AoHCI7vDkXrzzD24AlyzfV9Cp+ARYi9ZdMQftP2lONiFD+OPpp5JOjJpHVdhacEsnWadmxn2AJCuYF9fcil96mK8+2qcYQ02RwRFrnkFc52k2DuR0n19uBtHWvzHxnvkr5yPLD9dNqQa47o+Wk7nZMq9tgSRGXEIpV3sIREMISBEfzAswIDAQAB";

    private static final int RC_PAYPAL = 10002;

    public DokuAdapter(Fragment localPsFragment) {
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
        this.psPaypal = (PsDoku) params;
        this.psPaypal.setBundle(bundle);
        this.context = context;

//        try {
//            PwHttpClient.getSignature(context, psPaypal, new PwHttpClient.Callback() {
//                @Override
//                public void onError(int statusCode, String responseBody, Throwable error) {
//                    try {
//                        mthError.invoke(fragment);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onSuccess(int statusCode, String responseBody) {
//
//                    Log.i("RESPONSE_BODY", responseBody);
//                    try {
//                        JSONObject rootObj = new JSONObject(responseBody);
//                        if (rootObj.has("data")) {
//                            JSONObject dataObject = rootObj.getJSONObject("data");
//                            psPaypal.setAppId(dataObject.getString("appid"));
//                            psPaypal.setMerchantId(dataObject.getString("partnerid"));
//                            psPaypal.setNonceStr(dataObject.getString("noncestr"));
//                            psPaypal.setPrepayId(dataObject.getString("prepayid"));
////                            psPaypal.setOutTradeNo(dataObject.getString("out_trade_no"));
////                            psPaypal.setTotalFee(dataObject.getString("total_fee"));
////                            psPaypal.setNotifyUrl(dataObject.getString("notify_url"));
////                            psPaypal.setTradeType(dataObject.getString("trade_type"));
////                            psPaypal.setBody(dataObject.getString("body"));
////                            psPaypal.setCreateIp(IpAddress.getIPAddress(true));
//                            psPaypal.setPackageValue(dataObject.getString("package"));
//                            psPaypal.setTimeStamp(dataObject.getInt("timestamp") + "");
//                            psPaypal.setSignature(dataObject.getString("sign"));
////                            getPrepaidId();
//                            processPayment(context, psPaypal);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onStart() {
//                    try {
//                        mthShowWait.invoke(fragment);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onStop() {
//                    try {
//                        mthHideWait.invoke(fragment);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try{


        }catch(Exception e){

        }

    }
}

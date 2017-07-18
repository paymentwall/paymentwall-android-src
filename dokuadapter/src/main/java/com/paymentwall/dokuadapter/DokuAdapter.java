package com.paymentwall.dokuadapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by nguyen.anh on 2/24/2017.
 */

public class DokuAdapter {

    private PsDoku psDoku;
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

    private void pay(final Context context, Parcelable params, Map<String, String> bundle, Map<String, String> customParams) {
        this.psDoku = (PsDoku) params;
        this.psDoku.setBundle(bundle);
        this.psDoku.setCustomParams(customParams);
        this.context = context;

//        try {
//            PwHttpClient.getSignature(context, psDoku, new PwHttpClient.Callback() {
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
//                            psDoku.setAppId(dataObject.getString("appid"));
//                            psDoku.setMerchantId(dataObject.getString("partnerid"));
//                            psDoku.setNonceStr(dataObject.getString("noncestr"));
//                            psDoku.setPrepayId(dataObject.getString("prepayid"));
////                            psDoku.setOutTradeNo(dataObject.getString("out_trade_no"));
////                            psDoku.setTotalFee(dataObject.getString("total_fee"));
////                            psDoku.setNotifyUrl(dataObject.getString("notify_url"));
////                            psDoku.setTradeType(dataObject.getString("trade_type"));
////                            psDoku.setBody(dataObject.getString("body"));
////                            psDoku.setCreateIp(IpAddress.getIPAddress(true));
//                            psDoku.setPackageValue(dataObject.getString("package"));
//                            psDoku.setTimeStamp(dataObject.getInt("timestamp") + "");
//                            psDoku.setSignature(dataObject.getString("sign"));
////                            getPrepaidId();
//                            processPayment(context, psDoku);
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

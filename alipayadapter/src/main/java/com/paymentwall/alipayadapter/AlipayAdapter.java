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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
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

                    //Log.i("RESPONSE_BODY", responseBody);
                    try {
                        JSONObject rootObj = new JSONObject(responseBody);
                        if (rootObj.has("data")) {
                            JSONObject dataObject = rootObj.getJSONObject("data");
                            Iterator<String> keyIterator = dataObject.keys();
                            while (keyIterator.hasNext()) {
                                String key = keyIterator.next();
                                if(!dataObject.isNull(key)) {
                                    JSONObject tempObject = dataObject.optJSONObject(key);
                                    JSONArray tempArray = dataObject.optJSONArray(key);
                                    if(tempObject == null && tempArray == null) {
                                        psAlipay.put(key, dataObject.optString(key));
                                    }
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
                    //Log.i("AliAdapter","send to alipay orderInfo = "+orderInfo);
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
        String orderInfo = "";
        orderInfo = PsAlipay.buildAlipayParam(psAlipay.getAlipayParams());
        StringBuilder stringBuilder = new StringBuilder();
        for(Map.Entry<String, String> entry : psAlipay.getAlipayParams().entrySet()) {
            stringBuilder.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        //Log.i("AliAdapter", "orderInfo = "+orderInfo);
        return orderInfo;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case ALIPAY_ID: {
                        PayResult2 payResult = new PayResult2((Map<String, String>) msg.obj);

                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        //Log.i("result_info", payResult.toString());

                        String resultStatus = payResult.getResultStatus();
                        Intent intent = new Intent();
                        if (TextUtils.equals(resultStatus, "9000")) {
                            mthSuccess.invoke(fragment);
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            mthCancel.invoke(fragment);
                        } else {
                            if (TextUtils.equals(resultStatus, "8000")) {

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

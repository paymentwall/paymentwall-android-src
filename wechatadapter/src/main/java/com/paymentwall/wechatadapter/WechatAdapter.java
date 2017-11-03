package com.paymentwall.wechatadapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.paymentwall.pwunifiedsdk.core.BaseFragment;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by nguyen.anh on 10/26/2016.
 */

public class WechatAdapter {

    private PsWechat psWechat;
    private Method mthSuccess, mthError, mthCancel, mthShowWait, mthHideWait;
    private Fragment fragment;
    private final String mMode = "01";
    private Context context;
    private Object api;
    private final int COMMAND_PAY_BY_WX = 5;
    private final int STATUS_OK = 0;
    private final int STATUS_CANCEL = -2;

    public WechatAdapter(Fragment localPsFragment) {
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
        this.psWechat = (PsWechat) params;
        this.psWechat.setBundle(bundle);
        this.psWechat.setCustomParams(customParams);
        this.context = context;

        try {
            PwHttpClient.getSignature(context, psWechat, new PwHttpClient.Callback() {
                @Override
                public void onError(int statusCode, String responseBody, Throwable error) {
                    try {
                        mthError.invoke(fragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, String responseBody) {

                    Log.i("RESPONSE_BODY", responseBody);
                    try {
                        JSONObject rootObj = new JSONObject(responseBody);
                        if (rootObj.has("data")) {

                            JSONObject dataObject = rootObj.getJSONObject("data");
                            psWechat.setAppId(dataObject.getString("appid"));
                            psWechat.setMerchantId(dataObject.getString("partnerid"));
                            psWechat.setNonceStr(dataObject.getString("noncestr"));
                            psWechat.setPrepayId(dataObject.getString("prepayid"));
                            psWechat.setPackageValue(dataObject.getString("package"));
                            psWechat.setTimeStamp(dataObject.getLong("timestamp") + "");
                            psWechat.setSignature(dataObject.getString("sign"));
                            processPayment(context, psWechat);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStart() {
                    try {
                        mthShowWait.invoke(fragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStop() {
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

//        psWechat.setBody(bundle.get("ITEM_NAME") + "");
//        psWechat.setNonceStr(NonceStringGenerator.getInstance().getNONCE_STR(16));
//        psWechat.setOutTradeNo("w" + System.currentTimeMillis());
//        psWechat.setTotalFee((int) ((Double) bundle.get("AMOUNT") * 100) + "");
//        psWechat.setNotifyUrl("http://paymentwall.com");
//        psWechat.setPackageValue("Sign=WXPay");
//        psWechat.setTimeStamp(String.valueOf(System.currentTimeMillis()).toString().substring(0, 10));
//        psWechat.setCreateIp(IpAddress.getIPAddress(true));
//
//        String information = psWechat.getPrepaidParams();
//        Log.v("information:", information);
//        getPrepaidId();
    }

    private void getPrepaidId() {
        PwHttpClient.getPrepaidId(context, psWechat, new PwHttpClient.Callback() {
            @Override
            public void onError(int statusCode, String responseBody, Throwable error) {
                try {
                    mthError.invoke(fragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, String responseBody) {
                try {
                    Log.i("WECHAT", responseBody);
                    Map<String, Object> map = Utils.parseXML(responseBody);
                    String prepaid_id = map.get("prepay_id").toString();
                    psWechat.setPrepayId(prepaid_id);

//                    Message msg = new Message();
//                    msg.obj = prepaid_id;
//                    msg.what = 100;
//                    handler.sendMessage(msg);
                    processPayment(context, psWechat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                try {
                    mthShowWait.invoke(fragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStop() {
                try {
                    mthHideWait.invoke(fragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void processPayment(Context context, PsWechat psWechat) {
        try {

            IWXAPI api = WXAPIFactory.createWXAPI(context, psWechat.getAppId());
            api.registerApp(psWechat.getAppId());
            PayReq payReq = new PayReq();
            payReq.appId = psWechat.getAppId();
            payReq.partnerId = psWechat.getMerchantId();
            payReq.prepayId = psWechat.getPrepayId();
            payReq.packageValue = psWechat.getPackageValue();
            payReq.nonceStr = psWechat.getNonceStr();
            payReq.timeStamp = psWechat.getTimeStamp();
            payReq.sign = psWechat.getSignature();
            api.sendReq(payReq);

        } catch (Exception e) {

        }
    }


    private void onNewIntent(Intent intent) {
        try {
            Class IWXAPIEventHandler = Class.forName("com.tencent.mm.sdk.openapi.IWXAPIEventHandler");
            InvocationHandler handler = new InvocationHandler() {
                @Override

                public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                    if (method.getName().equalsIgnoreCase("onResp")) {
                        Class BaseResp = Class.forName("com.tencent.mm.sdk.modelbase.BaseResp");
                        Object resp = BaseResp.cast(args[0]);


                        Method mthGetType = BaseResp.getMethod("getType");
                        if ((int) (mthGetType.invoke(resp)) == COMMAND_PAY_BY_WX) {
                            Field field = BaseResp.getDeclaredField("errCode");
                            field.setAccessible(true);
                            if ((int) (field.get(resp)) == STATUS_OK) {
                                mthSuccess.invoke(fragment);
                            } else if ((int) (field.get(resp)) == STATUS_CANCEL) {
                                mthCancel.invoke(fragment);
                            } else {
                                mthError.invoke(fragment);
                            }
                        }
                    }
                    return null;
                }
            };

            Object proxy = Proxy.newProxyInstance(IWXAPIEventHandler.getClassLoader(), new Class<?>[]{IWXAPIEventHandler}, handler);
            Object listener = IWXAPIEventHandler.cast(proxy);

            Class<?> IWXAPI = Class.forName("com.tencent.mm.sdk.openapi.IWXAPI");
            Method mthHandleIntent = IWXAPI.getMethod("handleIntent", Intent.class, IWXAPIEventHandler);
            mthHandleIntent.invoke(api, intent, listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

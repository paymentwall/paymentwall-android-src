package com.paymentwall.moladapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by nguyen.anh on 10/24/2016.
 */

public class MolAdapter {

    private PsMol psMol;
    private Method mthSuccess, mthError, mthCancel, mthShowWait, mthHideWait;
    private Fragment fragment;
    private final String mMode = "01";

    public MolAdapter(Fragment localPsFragment){
        try {
            this.fragment = localPsFragment;
            Class<?> BaseFragment = Class.forName("com.paymentwall.pwunifiedsdk.core.BaseFragment");
            mthSuccess = BaseFragment.getMethod("onPaymentSuccessful");
            mthError = BaseFragment.getMethod("onPaymentError");
            mthCancel = BaseFragment.getMethod("onPaymentCancel");
            mthShowWait = BaseFragment.getMethod("showWaitLayout");
            mthHideWait = BaseFragment.getMethod("hideWaitLayout");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void pay(final Context context, Parcelable params, Map<String, String> bundle, Map<String, String> customParams) {
        this.psMol = (PsMol) params;
        this.psMol.setBundle(bundle);
        this.psMol.setCustomParams(customParams);

        try {
            processPayment(context, psMol);

//            PwlHttpClient.getSignature(context, psMol, new PwlHttpClient.Callback() {
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
////                            if (dataObject.has("version")) {
////                                psMol.setVersion(dataObject.getString("service"));
////                            }
////                            if (dataObject.has("partner")) {
////                                psMol.setPartnerId(dataObject.getString("partner"));
////                            }
////                            if (dataObject.has("_input_charset")) {
////                                psMol.setInputCharset(dataObject.getString("_input_charset"));
////                            }
////                            if (dataObject.has("sign_type")) {
////                                psMol.setSignType(dataObject.getString("sign_type"));
////                            }
////                            if (dataObject.has("notify_url")) {
////                                psMol.setNotifyUrl(dataObject.getString("notify_url"));
////                            }
////                            if (dataObject.has("out_trade_no")) {
////                                psMol.setOutTradeNo(dataObject.getString("out_trade_no"));
////                            }
////                            if (dataObject.has("subject")) {
////                                psMol.setSubject(dataObject.getString("subject"));
////                            }
////                            if (dataObject.has("payment_type")) {
////                                psMol.setPaymentType(dataObject.getString("payment_type"));
////                            }
////                            if (dataObject.has("seller_id")) {
////                                psMol.setSellerId(dataObject.getString("seller_id"));
////                            }
////                            if (dataObject.has("total_fee")) {
////                                psMol.setTotalFee(dataObject.getInt("total_fee") + "");
////                            }
////                            if (dataObject.has("body")) {
////                                psMol.setBody(dataObject.getString("body"));
////                            }
////                            if (dataObject.has("sign")) {
////                                psMol.setSignature(dataObject.getString("sign"));
////                            }
//
////                            JSONObject responseObj = dataObject.getJSONObject("response");
////                            if(responseObj.has("tn")){
////                                psMol.setTransactionNumber(responseObj.getString("tn"));
////                            }
////
//
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processPayment(Context context, PsMol psMol) {
        try {
            Class<?> MolPayment = Class.forName("com.mol.payment.MOLPayment");

            Method mthSetTestMode = MolPayment.getMethod("setTestMode", boolean.class);
            mthSetTestMode.invoke(null, true);

            Constructor constructor =
                    MolPayment.getConstructor(new Class[]{Context.class, String.class, String.class});
            Object mol = constructor.newInstance(context, psMol.getSecretKey(), psMol.getAppKey());
            Log.i("MOL", psMol.getSecretKey() + "-" + psMol.getAppKey());
            Double strAmount = Double.parseDouble(psMol.getBundle().get("AMOUNT") + "");
            long amount = (long) (strAmount * 100);
            Bundle inputBundle = new Bundle();
            inputBundle.putString("referenceId", makeReferenceId()); // Must
            inputBundle.putLong("amount", amount);
            inputBundle.putString("currencyCode", (String)psMol.getBundle().get("CURRENCY"));
            inputBundle.putString("description", (String)psMol.getBundle().get("ITEM_NAME")); // Optional
            inputBundle.putString("customerId", (String)psMol.getBundle().get("USER_ID"));


            Log.i("MOL_ADAPTER", inputBundle.toString());

            Class PaymentListener = Class.forName("com.mol.payment.PaymentListener");

            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                    if(method.getName().equalsIgnoreCase("onBack")) {
                        Bundle outBundle = (Bundle) args[1];
                        Log.i("MOL", outBundle.toString());

                        if(outBundle.getString("result").equalsIgnoreCase("A10000")){
                            //payment successful
                            mthSuccess.invoke(fragment);
                        }
                        else if(outBundle.getString("result").equalsIgnoreCase("A10002")){
                            //payment cancel
                            mthCancel.invoke(fragment);
                        }else{
                            //payment error
                            mthError.invoke(fragment);
                        }
                    }
                    return null;
                }
            };

            Object proxy = Proxy.newProxyInstance(PaymentListener.getClassLoader(), new Class<?>[]{PaymentListener}, handler);
            Object listener = PaymentListener.cast(proxy);

            Method mthPay = MolPayment.getMethod("pay", Context.class, Bundle.class, PaymentListener);
            mthPay.invoke(mol, context, inputBundle, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPaying(Context context, PsMol psMol) {
//        try {
//            Class<?> UPPayAssistEx = Class.forName("com.unionpay.UPPayAssistEx");
//            Method mthStartPay = UPPayAssistEx.getMethod("startPay", Context.class, String.class, String.class, String.class, String.class);
//            mthStartPay.invoke(UPPayAssistEx, context, null, null, psMol.getTransactionNumber(), mMode);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public String makeReferenceId() {
        return "RID" + (System.currentTimeMillis() & 0xFFFFFFFFL);
    }
}

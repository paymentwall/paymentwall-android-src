package com.paymentwall.unionpayadapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.paymentwall.pwunifiedsdk.core.LocalPsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by nguyen.anh on 9/8/2016.
 */

public class UnionpayAdapter {

    private PsUnionpay psUnionpay;
    private Method mthSuccess, mthError, mthCancel, mthShowWait, mthHideWait;
    private Fragment fragment;
    private final String mMode = "01";
    private final int PERMISSION_RC = 0x1134;

    public UnionpayAdapter(Fragment localPsFragment) {
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

    private void pay(final Context context, Serializable params, Map<String, Object> bundle, Map<String, String> customParams) {
        this.psUnionpay = (PsUnionpay) params;
        this.psUnionpay.setBundle(bundle);
        this.psUnionpay.setCustomParams(customParams);

        LocalPsFragment.getInstance().requestPermission(Manifest.permission.READ_PHONE_STATE, PERMISSION_RC, new LocalPsFragment.IOnRequestPermissionCallback() {
            @Override
            public void callback(int requestCode, int status) {
                if(requestCode == PERMISSION_RC && status == LocalPsFragment.PERMISSION_GRANTED){
                    try {
                        PwHttpClient.getSignature(context, psUnionpay, new PwHttpClient.Callback() {
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
//                            if (dataObject.has("version")) {
//                                psUnionpay.setVersion(dataObject.getString("service"));
//                            }
                                        JSONObject responseObj = dataObject.getJSONObject("response");
                                        if (responseObj.has("tn")) {
                                            psUnionpay.setTransactionNumber(responseObj.getString("tn"));
                                        }
                                        processUnionpayPayment(context, psUnionpay);
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
                }
            }
        });

    }

    private void processUnionpayPayment(Context context, PsUnionpay psUnionpay) {
        try {
            Class<?> UPPayAssistEx = Class.forName("com.unionpay.UPPayAssistEx");
            Method mthCheckInstalled = UPPayAssistEx.getMethod("checkInstalled", Context.class);
            boolean installed = (boolean) mthCheckInstalled.invoke(UPPayAssistEx, context);
            if (!installed) {
                Method mthInstallPlugin = UPPayAssistEx.getMethod("installUPPayPlugin", Context.class);
                mthInstallPlugin.invoke(UPPayAssistEx, context);
            } else {
                startPaying(context, psUnionpay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPaying(Context context, PsUnionpay psUnionpay) {
        try {
            Class<?> UPPayAssistEx = Class.forName("com.unionpay.UPPayAssistEx");
            Method mthStartPay = UPPayAssistEx.getMethod("startPay", Context.class, String.class, String.class, String.class, String.class);
            mthStartPay.invoke(UPPayAssistEx, context, null, null, psUnionpay.getTransactionNumber(), mMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (data == null) {
                mthError.invoke(fragment);
                return;
            }
            if (!data.getExtras().containsKey("pay_result")) {
                mthError.invoke(fragment);
                return;
            }

            String str = data.getExtras().getString("pay_result");
            if (str.equalsIgnoreCase("success")) {
                if (data.hasExtra("result_data")) {
                    String result = data.getExtras().getString("result_data");
                    try {
                        JSONObject resultJson = new JSONObject(result);
                        String sign = resultJson.getString("sign");
                        String dataOrg = resultJson.getString("data");

                        boolean ret = verify(dataOrg, sign, mMode);
                        if (ret) {
                            //payment successful
                            mthSuccess.invoke(fragment);
                        } else {
                            //payment failed
                            mthError.invoke(fragment);
                        }
                    } catch (JSONException e) {
                    }
                } else {
                    //payment successful
                    mthSuccess.invoke(fragment);
                }
            } else if (str.equalsIgnoreCase("fail")) {
                //payment failed
                mthError.invoke(fragment);
            } else if (str.equalsIgnoreCase("cancel")) {
                //payment cancelled
                mthCancel.invoke(fragment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verify(String msg, String sign64, String mode) {
        // 此处的verify，商户需送去商户后台做验签
        return true;
    }


}

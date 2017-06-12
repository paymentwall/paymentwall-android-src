package com.paymentwall.paypaladapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

//import com.paypal.android.sdk.payments.PayPalPayment;
//import com.paypal.android.sdk.payments.PayPalService;
//import com.paypal.android.sdk.payments.PaymentActivity;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by nguyen.anh on 2/21/2017.
 */

public class PaypalAdapter {

    private PsPaypal psPaypal;
    private Method mthSuccess, mthError, mthCancel, mthShowWait, mthHideWait;
    private Fragment fragment;
    private Context context;
    private Object api;


    private static final int RC_PAYPAL = 10002;

    public PaypalAdapter(Fragment localPsFragment) {
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
        this.psPaypal = (PsPaypal) params;
        this.psPaypal.setBundle(bundle);
        this.context = context;

        try {
            PwHttpClient.getSignature(context, psPaypal, new PwHttpClient.Callback() {
                @Override
                public void onError(int statusCode, String responseBody, Throwable error) {
                    Log.i("ON_ERROR", responseBody);
                    try {
                        mthError.invoke(fragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, String responseBody) {

                    Log.i("ON_SUCCESS", responseBody);
                    try {
                        JSONObject rootObj = new JSONObject(responseBody);
                        if (rootObj.has("data")) {
                            JSONObject dataObject = rootObj.getJSONObject("data");
                            psPaypal.setClickId(dataObject.getString("custom"));
                            psPaypal.setClientId(dataObject.getString("client_id"));
                            psPaypal.setIntent(dataObject.getString("intent"));
                            processPayment();
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

    private void processPayment() {
        try {

            Class<?> PayPalConfiguration = Class.forName("com.paypal.android.sdk.payments.PayPalConfiguration");
            Constructor consPayPalConfiguration = PayPalConfiguration.getConstructor();
            Object config = consPayPalConfiguration.newInstance();
            Method mthEnvironment = PayPalConfiguration.getMethod("environment", String.class);
            config = mthEnvironment.invoke(config, psPaypal.getEnvironment());
            Method mthClientId = PayPalConfiguration.getMethod("clientId", String.class);
            config = mthClientId.invoke(config, psPaypal.getClientId());
            config = PayPalConfiguration.cast(config);

            Class<?> PayPalService = Class.forName("com.paypal.android.sdk.payments.PayPalService");
            Intent in = new Intent(this.fragment.getActivity(), PayPalService);
            in.putExtra((String) ((PayPalService.getDeclaredField("EXTRA_PAYPAL_CONFIGURATION")).get(null)), (Parcelable) config);
            this.fragment.getActivity().startService(in);

//            PayPalConfiguration config = new PayPalConfiguration().environment(psPaypal.getEnvironment()).clientId(psPaypal.getClientId());
//            PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal((Double) psPaypal.getBundle().get("AMOUNT")),
//                    (String) psPaypal.getBundle().get("CURRENCY"), (String) psPaypal.getBundle().get("ITEM_NAME"), PayPalPayment.PAYMENT_INTENT_SALE);
//            thingToBuy.custom("d65542189");
//            thingToBuy.invoiceNumber("d65542189");

//            Intent intent = new Intent(this.fragment.getActivity(), PaymentActivity.class);
//            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
//            this.fragment.getActivity().startActivityForResult(intent, RC_PAYPAL);

            Class<?> PayPalPayment = Class.forName("com.paypal.android.sdk.payments.PayPalPayment");
            Constructor constructor = PayPalPayment.getConstructor(new Class[]{BigDecimal.class, String.class, String.class, String.class});
            Object thingToBuy = constructor.newInstance(new BigDecimal((Double) psPaypal.getBundle().get("AMOUNT")),
                    (String) psPaypal.getBundle().get("CURRENCY"), (String) psPaypal.getBundle().get("ITEM_NAME"), (PayPalPayment.getDeclaredField("PAYMENT_INTENT_SALE")).get(null));
            Method mthCustom = PayPalPayment.getMethod("custom", String.class);
            mthCustom.invoke(thingToBuy, psPaypal.getClickId());
            Method mthInvoiceNumber = PayPalPayment.getMethod("invoiceNumber", String.class);
            mthInvoiceNumber.invoke(thingToBuy, psPaypal.getClickId());


            Class<?> PaymentActivity = Class.forName("com.paypal.android.sdk.payments.PaymentActivity");
            Intent intent = new Intent(this.fragment.getActivity(), PaymentActivity);
            intent.putExtra((String) ((PayPalService.getDeclaredField("EXTRA_PAYPAL_CONFIGURATION")).get(null)), (Parcelable) config);
            intent.putExtra((String) ((PaymentActivity.getDeclaredField("EXTRA_PAYMENT")).get(null)), (Parcelable) thingToBuy);
            dumpIntent(intent);
            this.fragment.getActivity().startActivityForResult(intent, RC_PAYPAL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dumpIntent(Intent intent) {
        if (intent == null) return;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("Intent", String.format("%s - %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

    }

    private void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PAYPAL) {
            try {
                Class<?> PaymentConfirmation = Class.forName("com.paypal.android.sdk.payments.PaymentConfirmation");
                Class<?> PaymentActivity = Class.forName("com.paypal.android.sdk.payments.PaymentActivity");
                Field resultPaymentInvalid = PaymentActivity.getDeclaredField("RESULT_EXTRAS_INVALID");
                if (resultCode == Activity.RESULT_OK) {
                    Object confirm = data
                            .getParcelableExtra((String) ((PaymentActivity.getDeclaredField("EXTRA_RESULT_CONFIRMATION")).get(null)));
                    Method mthToJSONObject = PaymentConfirmation.getMethod("toJSONObject");
                    Object jsonObject = mthToJSONObject.invoke(confirm);
                    Log.i("PAYMENT_CONFIRMATION", jsonObject.toString());
                    if (confirm != null) {
                        mthSuccess.invoke(this.fragment);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    mthCancel.invoke(this.fragment);
                } else if (resultCode == (int) (resultPaymentInvalid.get(null))) {
                    mthError.invoke(this.fragment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

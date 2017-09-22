package com.paymentwall.mycardadapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by nguyen.anh on 5/15/2017.
 */

public class MycardAdapter {

    private PsMycard psMyCard;
    private Method mthSuccess, mthError, mthCancel, mthShowWait, mthHideWait, mthReplaceFragment;
    private Fragment fragment;
    private final String mMode = "01";
    private Context context;
    private Object psActivity;

    public MycardAdapter(Fragment localPsFragment) {
        try {
            this.fragment = localPsFragment;
            Class<?> BaseFragment = Class.forName("com.paymentwall.pwunifiedsdk.core.BaseFragment");
            mthSuccess = BaseFragment.getMethod("onPaymentSuccessful");
            mthError = BaseFragment.getMethod("onPaymentError");
            mthCancel = BaseFragment.getMethod("onPaymentCancel");
            mthShowWait = BaseFragment.getMethod("showWaitLayout");
            mthHideWait = BaseFragment.getMethod("hideWaitLayout");

            Activity activity = localPsFragment.getActivity();
            Class<?> PaymentSelectionActivity = Class.forName("com.paymentwall.pwunifiedsdk.core.PaymentSelectionActivity");
            psActivity = PaymentSelectionActivity.cast(activity);
            mthReplaceFragment = PaymentSelectionActivity.getMethod("replaceContentFragment", Fragment.class, Bundle.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pay(final Context context, Parcelable params, Map<String, String> bundle, Map<String, String> customParams) {
        psMyCard = (PsMycard) params;
        psMyCard.setBundle(bundle);
        psMyCard.setCustomParams(customParams);
        Bundle b = new Bundle();
        b.putParcelable(MycardFragment.MYCARD_OBJECT, psMyCard);
        try {
            mthReplaceFragment.invoke(psActivity, MycardFragment.getInstance(), b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

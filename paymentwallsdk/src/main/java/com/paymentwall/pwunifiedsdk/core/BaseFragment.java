package com.paymentwall.pwunifiedsdk.core;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paymentwall.pwunifiedsdk.R;
import com.paymentwall.pwunifiedsdk.ui.WaveHelper;
import com.paymentwall.pwunifiedsdk.ui.WaveView;
import com.paymentwall.pwunifiedsdk.util.PwUtils;
import com.paymentwall.pwunifiedsdk.util.ResponseCode;

/**
 * Created by nguyen.anh on 7/15/2016.
 */

public class BaseFragment extends Fragment {

    public Activity self;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = getActivity();
    }

    public PaymentSelectionActivity getMainActivity() {
        return (PaymentSelectionActivity) getActivity();
    }

    public void showKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                self.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            view.requestFocus();
            inputManager.showSoftInput(view,
                    InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) self
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = self.getCurrentFocus();
        if (view != null) {
//            view.clearFocus();
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showWaitLayout() {
        getMainActivity().showWaitLayout();
    }

    public void hideWaitLayout() {
        getMainActivity().hideWaitLayout();
    }

    public void showErrorLayout(final String error) {
        getMainActivity().showErrorLayout(error);
    }

    public void hideErrorLayout() {
        getMainActivity().hideErrorLayout();
    }

    public void displayPaymentSucceeded() {
        getMainActivity().displayPaymentSucceeded();
    }

    public void onPaymentSuccessful() {

    }

    public void onPaymentError() {

    }

    public void onPaymentCancel() {

    }

    public void onPaymentError(String error){

    }


}

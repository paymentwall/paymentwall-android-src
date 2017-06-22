package com.paymentwall.pwunifiedsdk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.io.Serializable;

/**
 * Created by nguyen.anh on 9/9/2016.
 */

public class ExtPsLayout extends LinearLayout {

    private String psId;
    private Serializable params;

    public ExtPsLayout(Context context) {
        super(context);
    }

    public ExtPsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtPsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getPsId() {
        return psId;
    }

    public void setPsId(String psId) {
        this.psId = psId;
    }

    public Serializable getParams() {
        return params;
    }

    public void setParams(Serializable params) {
        this.params = params;
    }
}

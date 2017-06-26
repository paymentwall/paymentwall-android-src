package com.paymentwall.pwunifiedsdk.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by nguyen.anh on 9/9/2016.
 */

public class ExternalPs implements Serializable {

    private String id;
    private String displayName;
    private int iconResId;
    private Serializable params;

    public ExternalPs(String id , String displayName, int iconResId, Serializable params){
        this.id = id;
        this.displayName = displayName;
        this.iconResId = iconResId;
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public Serializable getParams() {
        return params;
    }

    public void setParams(Serializable params) {
        this.params = params;
    }

}

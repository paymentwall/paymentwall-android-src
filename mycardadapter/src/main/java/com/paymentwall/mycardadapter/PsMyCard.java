package com.paymentwall.mycardadapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.paymentwall.pwunifiedsdk.util.MiscUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by nguyen.anh on 5/15/2017.
 */

public class PsMycard implements Parcelable {

    private String pwSign;
    private Map<String, String> bundle;
    private Map<String, String> customParams;

    public Map<String, String> getBundle() {
        return bundle;
    }

    public void setBundle(Map<String, String> bundle) {
        this.bundle = bundle;
    }

    public boolean isTestMode() {
        return (bundle.get("TEST_MODE") + "").equalsIgnoreCase("true");
    }

    public String getPwSign() {
        return pwSign;
    }

    public void setPwSign(String pwSign) {
        this.pwSign = pwSign;
    }

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, String> customParams) {
        this.customParams = customParams;
    }

    public Map<String, String> getInitTransactionParameterMap() {

        TreeMap<String, String> parametersMap = new TreeMap<String, String>();
        parametersMap.put("key", (String) bundle.get("PW_PROJECT_KEY"));
        parametersMap.put("uid", (String) bundle.get("USER_ID"));
        parametersMap.put("amount", bundle.get("AMOUNT") + "");
        parametersMap.put("currency", (String) bundle.get("CURRENCY"));
        parametersMap.put("ps", "mycardcard");
        parametersMap.put("product_id", (String) bundle.get("ITEM_ID"));
        parametersMap.put("product_name", (String) bundle.get("ITEM_NAME"));
        parametersMap.put("sign_version", "3");

        String orderInfo = MiscUtils.printPwMap(MiscUtils.sortMap(parametersMap));
        orderInfo += bundle.get("PW_PROJECT_SECRET");
        String sign = "";
        sign = MiscUtils.sha256(orderInfo);
        parametersMap.put("sign", sign);
        Log.i("ORDER_INFO", orderInfo);
        Log.i("SIGN", sign);

        return parametersMap;
    }

    public Map<String, String> makeTransactionProcessingMap(Map<String, String> parametersMap) {

        String orderInfo = MiscUtils.printPwMap(MiscUtils.sortMap(parametersMap));
        orderInfo += bundle.get("PW_PROJECT_SECRET");
//        try {
//            orderInfo = URLEncoder.encode(orderInfo, "UTF-8");
//        }catch(UnsupportedEncodingException e){
//            e.printStackTrace();
//        }
        String sign = "";
        if (parametersMap.get("sign_version").equalsIgnoreCase("3"))
            sign = MiscUtils.sha256(orderInfo);
        else if (parametersMap.get("sign_version").equalsIgnoreCase("2"))
            sign = MiscUtils.md5(orderInfo);
        parametersMap.put("sign", sign);
        Log.i("ORDER_INFO", orderInfo);
        Log.i("SIGN", sign);

        return parametersMap;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pwSign);
        dest.writeInt(this.bundle.size());
        for (Map.Entry<String, String> entry : this.bundle.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeInt(this.customParams.size());
        for (Map.Entry<String, String> entry : this.customParams.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public PsMycard() {
        this.customParams = new HashMap<>();
        this.bundle = new HashMap<>();
    }

    protected PsMycard(Parcel in) {
        this.pwSign = in.readString();
        int bundleSize = in.readInt();
        this.bundle = new HashMap<String, String>(bundleSize);
        for (int i = 0; i < bundleSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.bundle.put(key, value);
        }
        int customParamsSize = in.readInt();
        this.customParams = new HashMap<String, String>(customParamsSize);
        for (int i = 0; i < customParamsSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.customParams.put(key, value);
        }
    }

    public static final Creator<PsMycard> CREATOR = new Creator<PsMycard>() {
        @Override
        public PsMycard createFromParcel(Parcel source) {
            return new PsMycard(source);
        }

        @Override
        public PsMycard[] newArray(int size) {
            return new PsMycard[size];
        }
    };
}

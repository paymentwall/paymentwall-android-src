package com.paymentwall.baiduadapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by nguyen.anh on 12/6/2016.
 */

public class PsBaidu implements Serializable, Parcelable {

    private Map<String, String> bundle, customParams;

    public Map<String, String> getBundle() {
        return bundle;
    }

    public void setBundle(Map<String, String> bundle) {
        this.bundle = bundle;
    }

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, String> customParams) {
        this.customParams = customParams;
    }

    public boolean isTestMode(){
        return (bundle.get("TEST_MODE") + "").equalsIgnoreCase("true");
    }

    public Map<String, String> getWallApiParameterMap() {
        TreeMap<String, String> parametersMap = new TreeMap<String, String>();
        parametersMap.put("uid", (String)bundle.get("USER_ID"));
        parametersMap.put("key", (String)bundle.get("PW_PROJECT_KEY"));
        parametersMap.put("ag_name", (String)bundle.get("ITEM_NAME"));
        parametersMap.put("ag_external_id", (String)bundle.get("ITEM_ID"));
        parametersMap.put("amount", bundle.get("AMOUNT") + "");
        parametersMap.put("currencyCode", (String)bundle.get("CURRENCY"));
        parametersMap.put("sign_version", bundle.get("SIGN_VERSION") + "");

        parametersMap.put("ps_name", "alipay");


        String orderInfo = printWallApiMap(sortMap(parametersMap));
        orderInfo += bundle.get("PW_PROJECT_SECRET");
        String sign = sha256(orderInfo);
        parametersMap.put("sign", sign);
        parametersMap.remove("ps_name");
        Log.i("ORDER_INFO", orderInfo);
        Log.i("SIGN", sign);

        return parametersMap;
    }

    public static Map<String, String> sortMap(Map<String, String> aMap) {
        Map<String, String> sortedMap = new TreeMap<String, String>(aMap);
        return sortedMap;
    }

    public static String printWallApiMap(Map<String, String> params) {
        StringBuilder out = new StringBuilder();
        final Set<Map.Entry<String, String>> entrySet = params.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
//            out.append('|');
            out.append(entry.getKey());
            out.append('=');
            out.append(entry.getValue());

        }
        return out.toString();
    }

    public static String sha256(String inputString) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(inputString.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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

    public PsBaidu() {
        this.bundle = new HashMap<>();
        this.customParams = new HashMap<>();
    }

    protected PsBaidu(Parcel in) {
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

    public static final Parcelable.Creator<PsBaidu> CREATOR = new Parcelable.Creator<PsBaidu>() {
        @Override
        public PsBaidu createFromParcel(Parcel source) {
            return new PsBaidu(source);
        }

        @Override
        public PsBaidu[] newArray(int size) {
            return new PsBaidu[size];
        }
    };
}

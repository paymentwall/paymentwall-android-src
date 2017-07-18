package com.paymentwall.moladapter;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by nguyen.anh on 9/8/2016.
 */

public class PsMol implements Parcelable {

    private String appKey, secretKey, referenceId, description, customerId;
    private Map<String, String> bundle, customParams;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

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

    public Map<String, String> getWallApiParameterMap() {
        TreeMap<String, String> parametersMap = new TreeMap<String, String>();
        parametersMap.put("uid", (String) bundle.get("USER_ID"));
        parametersMap.put("key", (String) bundle.get("PW_PROJECT_KEY"));
        parametersMap.put("ag_name", (String) bundle.get("ITEM_NAME"));
        parametersMap.put("ag_external_id", (String) bundle.get("ITEM_ID"));
        parametersMap.put("amount", bundle.get("AMOUNT") + "");
        parametersMap.put("currencyCode", (String) bundle.get("CURRENCY"));
        parametersMap.put("sign_version", (String) bundle.get("SIGN_VERSION"));

        parametersMap.put("ps_name", "mol");

        String orderInfo = printMap(sortMap(parametersMap));
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

    public static String printMap(Map<String, String> params) {
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

    public static String printAlipayMap(Map<String, String> params) {
        StringBuilder out = new StringBuilder();
        final Set<Map.Entry<String, String>> entrySet = params.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
//            out.append('|');
            out.append(entry.getKey());
            out.append('=');
            out.append("\"" + entry.getValue() + "\"");
            out.append('&');
        }
        String printedStr = out.toString().substring(0, out.toString().length() - 1);
        return printedStr;
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

    public static String signRsa(Context context, String content, String privateKey) {
        try {

            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.decode(privateKey, Base64.DEFAULT));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance("SHA1WithRSA");

            signature.initSign(priKey);
            signature.update(content.getBytes("UTF-8"));

            byte[] signed = signature.sign();

            return Base64.encodeToString(signed, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String md5(String inputString) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
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
        dest.writeString(this.appKey);
        dest.writeString(this.secretKey);
        dest.writeString(this.referenceId);
        dest.writeString(this.description);
        dest.writeString(this.customerId);
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

    public PsMol() {
        this.bundle = new HashMap<>();
        this.customParams = new HashMap<>();
    }

    protected PsMol(Parcel in) {
        this.appKey = in.readString();
        this.secretKey = in.readString();
        this.referenceId = in.readString();
        this.description = in.readString();
        this.customerId = in.readString();
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

    public static final Creator<PsMol> CREATOR = new Creator<PsMol>() {
        @Override
        public PsMol createFromParcel(Parcel source) {
            return new PsMol(source);
        }

        @Override
        public PsMol[] newArray(int size) {
            return new PsMol[size];
        }
    };
}

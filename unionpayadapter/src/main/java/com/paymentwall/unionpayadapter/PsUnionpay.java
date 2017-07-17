package com.paymentwall.unionpayadapter;

import android.content.Context;
import android.os.Bundle;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by nguyen.anh on 9/8/2016.
 */

public class PsUnionpay implements Parcelable {

    private String signature;
    private String version, encoding, signMethod, txnType, txnSubType, bizType, channelType, accessType, merId, backUrl, orderId, txnTime, certId;
    private int txnAmt;
    private String transactionNumber;
    private Map<String, String> bundle;
    private Map<String, String> customParams;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnSubType() {
        return txnSubType;
    }

    public void setTxnSubType(String txnSubType) {
        this.txnSubType = txnSubType;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public int getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(int txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public boolean isTestMode() {
        return (bundle.get("TEST_MODE") + "").equalsIgnoreCase("true");
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
        parametersMap.put("sign_version", bundle.get("SIGN_VERSION") + "");

        if (customParams != null) {
            Iterator entries = customParams.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                parametersMap.put(entry.getKey() + "", entry.getValue() + "");
            }
        }

//        parametersMap.put("ps_name", "unionpay");

        String orderInfo = printMap(sortMap(parametersMap));
        orderInfo += bundle.get("PW_PROJECT_SECRET");
        String sign = sha256(orderInfo);
        parametersMap.put("sign", sign);
//        parametersMap.remove("ps_name");
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
        dest.writeString(this.signature);
        dest.writeString(this.version);
        dest.writeString(this.encoding);
        dest.writeString(this.signMethod);
        dest.writeString(this.txnType);
        dest.writeString(this.txnSubType);
        dest.writeString(this.bizType);
        dest.writeString(this.channelType);
        dest.writeString(this.accessType);
        dest.writeString(this.merId);
        dest.writeString(this.backUrl);
        dest.writeString(this.orderId);
        dest.writeString(this.txnTime);
        dest.writeString(this.certId);
        dest.writeInt(this.txnAmt);
        dest.writeString(this.transactionNumber);
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

    public PsUnionpay() {
        this.bundle = new HashMap<>();
        this.customParams = new HashMap<>();
    }

    protected PsUnionpay(Parcel in) {
        this.signature = in.readString();
        this.version = in.readString();
        this.encoding = in.readString();
        this.signMethod = in.readString();
        this.txnType = in.readString();
        this.txnSubType = in.readString();
        this.bizType = in.readString();
        this.channelType = in.readString();
        this.accessType = in.readString();
        this.merId = in.readString();
        this.backUrl = in.readString();
        this.orderId = in.readString();
        this.txnTime = in.readString();
        this.certId = in.readString();
        this.txnAmt = in.readInt();
        this.transactionNumber = in.readString();
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

    public static final Creator<PsUnionpay> CREATOR = new Creator<PsUnionpay>() {
        @Override
        public PsUnionpay createFromParcel(Parcel source) {
            return new PsUnionpay(source);
        }

        @Override
        public PsUnionpay[] newArray(int size) {
            return new PsUnionpay[size];
        }
    };
}

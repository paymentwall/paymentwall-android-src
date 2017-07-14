package com.paymentwall.alipayadapter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import java.io.Serializable;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by nguyen.anh on 9/6/2016.
 */

public class PsAlipay implements Parcelable {

    private String partnerId;
    private String sellerId;
    private String service;
    private String outTradeNo;
    private String inputCharset;
    private String currencyCode;
    private String signType;
    private String notifyUrl;
    private String subject;
    private String paymentType;
    private String totalFee;
    private String body;
    private String signature;
    private String itbPay;
    private String forexBiz;
    private String appId;
    private String appenv;
    private String privateKey;
    private String timeStamp;
    private String version;
    private String method;
    private String productCode;
    private String pwSign;
    private Map<String, String> params;
    private Map<String, String> customParams;


    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getItbPay() {
        return itbPay;
    }

    public void setItbPay(String itbPay) {
        this.itbPay = itbPay;
    }

    public String getForexBiz() {
        return forexBiz;
    }

    public void setForexBiz(String forexBiz) {
        this.forexBiz = forexBiz;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppenv() {
        return appenv;
    }

    public void setAppenv(String appenv) {
        this.appenv = appenv;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, String> customParams) {
        this.customParams = customParams;
    }

    public String getPwSign() {
        return pwSign;
    }

    public void setPwSign(String pwSign) {
        this.pwSign = pwSign;
    }

    public boolean isTestMode() {
        return (params.get("TEST_MODE") + "").equalsIgnoreCase("true");
    }

    public Map<String, String> getWallApiParameterMap() {
        TreeMap<String, String> parametersMap = new TreeMap<String, String>();
        parametersMap.put("uid", (String) params.get("USER_ID"));
        parametersMap.put("key", (String) params.get("PW_PROJECT_KEY"));
        parametersMap.put("ag_name", (String) params.get("ITEM_NAME"));
        parametersMap.put("ag_external_id", (String) params.get("ITEM_ID"));
        parametersMap.put("amount", params.get("AMOUNT") + "");
        Log.i("CURRENCY", (String) params.get("CURRENCY"));
        parametersMap.put("currencyCode", (String) params.get("CURRENCY"));
        parametersMap.put("sign_version", params.get("SIGN_VERSION") + "");

        if (getItbPay() != null) {
            parametersMap.put("it_b_pay", getItbPay());
        }

        if (getForexBiz() != null) {
            parametersMap.put("forex_biz", getForexBiz());
        }

        if (getAppId() != null) {
            parametersMap.put("app_id", getAppId());
        }

        if (getAppenv() != null) {
            parametersMap.put("appenv", getAppenv());
        }

        if (customParams != null) {
            Iterator entries = customParams.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                parametersMap.put(entry.getKey() + "", entry.getValue() + "");
            }
        }

        if (getPwSign() == null) {
//            parametersMap.put("ps_name", "alipay");
            String orderInfo = printWallApiMap(sortMap(parametersMap));
            orderInfo += params.get("PW_PROJECT_SECRET");
            Log.i("WallApi request", orderInfo);
            String sign = sha256(orderInfo);
            parametersMap.put("sign", sign);
//            parametersMap.remove("ps_name");
        } else {
            parametersMap.put("sign", getPwSign());
        }
//        Log.i("ORDER_INFO", orderInfo);
//        Log.i("SIGN", sign);

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

    public static String printInternationalMap(Map<String, String> params) {
        StringBuilder out = new StringBuilder();
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            out.append(pair.getKey());
            out.append('=');
            out.append("\"" + pair.getValue() + "\"");
            out.append('&');
        }

        String printedStr = out.toString().substring(0, out.toString().length() - 1);
        return printedStr;
    }

    public static String buildAlipayParam(Map<String, String> map) {
        List<String> keys = new ArrayList<>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }

    public static String getSign(Map<String, String> map, String rsaKey) {
        List<String> keys = new ArrayList<String>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, false));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, false));

        Log.i("SIGN_STRING", authInfo.toString());

        String oriSign = PsAlipay.signRsa(authInfo.toString(), rsaKey);
        String encodedSign = "";

        return oriSign;
    }

    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
//        sb.append("\"");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
//                sb.append(value);
            } catch (Exception e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
//        sb.append("\"");
        return sb.toString();
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

    public static String signRsa(String content, String privateKey) {
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


    public PsAlipay() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.partnerId);
        dest.writeString(this.sellerId);
        dest.writeString(this.service);
        dest.writeString(this.outTradeNo);
        dest.writeString(this.inputCharset);
        dest.writeString(this.currencyCode);
        dest.writeString(this.signType);
        dest.writeString(this.notifyUrl);
        dest.writeString(this.subject);
        dest.writeString(this.paymentType);
        dest.writeString(this.totalFee);
        dest.writeString(this.body);
        dest.writeString(this.signature);
        dest.writeString(this.itbPay);
        dest.writeString(this.forexBiz);
        dest.writeString(this.appId);
        dest.writeString(this.appenv);
        dest.writeString(this.privateKey);
        dest.writeString(this.timeStamp);
        dest.writeString(this.version);
        dest.writeString(this.method);
        dest.writeString(this.productCode);
        dest.writeString(this.pwSign);

//        dest.writeInt(this.params.size());
//        for (Map.Entry<String, String> entry : this.params.entrySet()) {
//            dest.writeString(entry.getKey());
//            dest.writeString(entry.getValue());
//        }
//        dest.writeInt(this.customParams.size());
//        for (Map.Entry<String, String> entry : this.customParams.entrySet()) {
//            dest.writeString(entry.getKey());
//            dest.writeString(entry.getValue());
//        }
    }

    protected PsAlipay(Parcel in) {
        this.partnerId = in.readString();
        this.sellerId = in.readString();
        this.service = in.readString();
        this.outTradeNo = in.readString();
        this.inputCharset = in.readString();
        this.currencyCode = in.readString();
        this.signType = in.readString();
        this.notifyUrl = in.readString();
        this.subject = in.readString();
        this.paymentType = in.readString();
        this.totalFee = in.readString();
        this.body = in.readString();
        this.signature = in.readString();
        this.itbPay = in.readString();
        this.forexBiz = in.readString();
        this.appId = in.readString();
        this.appenv = in.readString();
        this.privateKey = in.readString();
        this.timeStamp = in.readString();
        this.version = in.readString();
        this.method = in.readString();
        this.productCode = in.readString();
        this.pwSign = in.readString();

//        int paramsSize = in.readInt();

//        this.params = new HashMap<String, String>(paramsSize);
//        for (int i = 0; i < paramsSize; i++) {
//            String key = in.readString();
//            String value = in.readParcelable(Object.class.getClassLoader());
//            this.params.put(key, value);
//        }
//        int customParamsSize = in.readInt();
//        this.customParams = new HashMap<String, String>(customParamsSize);
//        for (int i = 0; i < customParamsSize; i++) {
//            String key = in.readString();
//            String value = in.readString();
//            this.customParams.put(key, value);
//        }
    }

    public static final Creator<PsAlipay> CREATOR = new Creator<PsAlipay>() {
        @Override
        public PsAlipay createFromParcel(Parcel source) {
            return new PsAlipay(source);
        }

        @Override
        public PsAlipay[] newArray(int size) {
            return new PsAlipay[size];
        }
    };
}

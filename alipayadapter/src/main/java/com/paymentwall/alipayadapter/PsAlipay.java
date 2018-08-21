package com.paymentwall.alipayadapter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by nguyen.anh on 9/6/2016.
 */

public class PsAlipay implements Parcelable {
    private static class B {

    }

    private static class Z {
        static final String SECONDARY_MERCHANT_ID = "secondary_merchant_id";
        static final String SECONDARY_MERCHANT_NAME = "secondary_merchant_name";
        static final String SECONDARY_MERCHANT_INDUSTRY = "secondary_merchant_industry";
        static final String RMB_FEE = "rmb_fee";
        static final String EXTERN_TOKEN = "extern_token";
        static final String SERVICE = "service";
        static final String PARTNER = "partner";
        static final String _INPUT_CHARSET = "_input_charset";
        static final String CHARSET = "charset";
        static final String SIGN_TYPE = "sign_type";
        static final String NOTIFY_URL = "notify_url";
        static final String OUT_TRADE_NO = "out_trade_no";
        static final String SUBJECT = "subject";
        static final String PAYMENT_TYPE = "payment_type";
        static final String SELLER_ID = "seller_id";
        static final String TOTAL_FEE = "total_fee";
        static final String BODY = "body";
        static final String CURRENCY = "currency";
        static final String IT_B_PAY = "it_b_pay";
        static final String FOREX_BIZ = "forex_biz";
        static final String APP_ID = "app_id";
        static final String METHOD = "method";
        static final String APPENV = "appenv";
        static final String SIGN = "sign";
        static final String TIMESTAMP = "timestamp";
        static final String VERSION = "version";
        static final String BIZ_CONTENT = "biz_content";
    }

    /*private String partnerId;
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
    private String pwSign;*/
    private String pwSign;
    private Map<String, String> alipayParams = new TreeMap<>();
    private Map<String, String> params;
    private Map<String, String> customParams;
    private String privateKey;

    public Map<String, String> getAlipayParams() {
        return alipayParams;
    }

    public void setAlipayParams(Map<String, String> alipayParams) {
        this.alipayParams = alipayParams;
    }

    public void put(String key, String value) {
        alipayParams.put(key, value);
    }

    public String get(String key) {
        return alipayParams.get(key);
    }

    public void setSecondaryMerchantId(String secondaryMerchantId) {
        if (secondaryMerchantId == null) alipayParams.remove(Z.SECONDARY_MERCHANT_ID);
        else alipayParams.put(Z.SECONDARY_MERCHANT_ID, secondaryMerchantId);
    }

    public void setSecondaryMerchantName(String secondaryMerchantName) {
        if (secondaryMerchantName == null) alipayParams.remove(Z.SECONDARY_MERCHANT_NAME);
        else alipayParams.put(Z.SECONDARY_MERCHANT_NAME, secondaryMerchantName);
    }

    public void setSecondaryMerchantIndustry(String secondaryMerchantIndustry) {
        if (secondaryMerchantIndustry == null) alipayParams.remove(Z.SECONDARY_MERCHANT_INDUSTRY);
        else alipayParams.put(Z.SECONDARY_MERCHANT_INDUSTRY, secondaryMerchantIndustry);
    }

    public void setExternToken(String externToken) {
        if (externToken == null) alipayParams.remove(Z.EXTERN_TOKEN);
        else alipayParams.put(Z.EXTERN_TOKEN, externToken);
    }

    public void setRmbFee(BigDecimal rmbFee) {
        if (rmbFee == null) alipayParams.remove(Z.RMB_FEE);
        else alipayParams.put(Z.RMB_FEE, rmbFee.toPlainString());
    }

    public void setRmbFee(String rmbFee) {
        if (rmbFee == null) alipayParams.remove(Z.RMB_FEE);
        else alipayParams.put(Z.RMB_FEE, rmbFee);
    }

    public String getSecondaryMerchantId() {
        return alipayParams.get(Z.SECONDARY_MERCHANT_ID);
    }

    public String getSecondaryMerchantName() {
        return alipayParams.get(Z.SECONDARY_MERCHANT_NAME);
    }

    public String getSecondaryMerchantIndustry() {
        return alipayParams.get(Z.SECONDARY_MERCHANT_INDUSTRY);
    }

    public String getPartnerId() {
        return alipayParams.get(Z.PARTNER);
    }

    public void setPartnerId(String partnerId) {
        if (partnerId == null) alipayParams.remove(Z.PARTNER);
        else alipayParams.put(Z.PARTNER, partnerId);
    }

    public String getSellerId() {
        return alipayParams.get(Z.SELLER_ID);
    }

    public void setSellerId(String sellerId) {
        if (sellerId == null) alipayParams.remove(Z.SELLER_ID);
        else alipayParams.put(Z.SELLER_ID, sellerId);
    }

    public String getService() {
        return alipayParams.get(Z.SERVICE);
    }

    public void setService(String service) {
        if (service == null) alipayParams.remove(Z.SERVICE);
        else alipayParams.put(Z.SERVICE, service);
    }

    public String getOutTradeNo() {
        return alipayParams.get(Z.OUT_TRADE_NO);
    }

    public void setOutTradeNo(String outTradeNo) {
        if (outTradeNo == null) alipayParams.remove(Z.OUT_TRADE_NO);
        else alipayParams.put(Z.OUT_TRADE_NO, outTradeNo);
    }

    public String getInputCharset() {
        return alipayParams.get(Z._INPUT_CHARSET);
    }

    public void setInputCharset(String inputCharset) {
        if (inputCharset == null) alipayParams.remove(Z._INPUT_CHARSET);
        else alipayParams.put(Z._INPUT_CHARSET, inputCharset);
    }

    public String getCharset() {
        return alipayParams.get(Z.CHARSET);
    }

    public void setCharset(String charset) {
        if (charset == null) alipayParams.remove(Z.CHARSET);
        else alipayParams.put(Z.CHARSET, charset);
    }

    public String getCurrencyCode() {
        return alipayParams.get(Z.CURRENCY);
    }

    public void setCurrencyCode(String currencyCode) {
        if (currencyCode == null) alipayParams.remove(Z.CURRENCY);
        else alipayParams.put(Z.CURRENCY, currencyCode);
    }

    public String getSignType() {
        return alipayParams.get(Z.SIGN_TYPE);
    }

    public void setSignType(String signType) {
        if (signType == null) alipayParams.remove(Z.SIGN_TYPE);
        else alipayParams.put(Z.SIGN_TYPE, signType);
    }

    public String getNotifyUrl() {
        return alipayParams.get(Z.NOTIFY_URL);
    }

    public void setNotifyUrl(String notifyUrl) {
        if (notifyUrl == null) alipayParams.remove(Z.NOTIFY_URL);
        else {
            notifyUrl = notifyUrl.replace("\\/", "/");
            alipayParams.put(Z.NOTIFY_URL, notifyUrl);
        }
    }

    public String getSubject() {
        return alipayParams.get(Z.SUBJECT);
    }

    public void setSubject(String subject) {
        if (subject == null) alipayParams.remove(Z.SUBJECT);
        else alipayParams.put(Z.SUBJECT, subject);
    }

    public String getPaymentType() {
        return alipayParams.get(Z.PAYMENT_TYPE);
    }

    public void setPaymentType(String paymentType) {
        if (paymentType == null) alipayParams.remove(Z.PAYMENT_TYPE);
        else alipayParams.put(Z.PAYMENT_TYPE, paymentType);
    }

    public String getTotalFee() {
        return alipayParams.get(Z.TOTAL_FEE);
    }

    public void setTotalFee(String totalFee) {
        if (totalFee == null) alipayParams.remove(Z.TOTAL_FEE);
        else alipayParams.put(Z.TOTAL_FEE, totalFee);
    }

    public String getBody() {
        return alipayParams.get(Z.BODY);
    }

    public void setBody(String body) {
        if (body == null) alipayParams.remove(Z.BODY);
        else alipayParams.put(Z.BODY, body);
    }

    public String getSignature() {
        return alipayParams.get(Z.SIGN);
    }

    public void setSignature(String signature) {
        if (signature == null) alipayParams.remove(Z.SIGN);
        else {
            signature = signature.replace("\\/", "/");
            alipayParams.put(Z.SIGN, signature);
        }
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getItbPay() {
        return alipayParams.get(Z.IT_B_PAY);
    }

    public void setItbPay(String itbPay) {
        if (itbPay == null) alipayParams.remove(Z.IT_B_PAY);
        else alipayParams.put(Z.IT_B_PAY, itbPay);
    }

    public String getForexBiz() {
        return alipayParams.get(Z.FOREX_BIZ);
    }

    public void setForexBiz(String forexBiz) {
        if (forexBiz == null) alipayParams.remove(Z.FOREX_BIZ);
        else alipayParams.put(Z.FOREX_BIZ, forexBiz);
    }

    public String getAppId() {
        return alipayParams.get(Z.APP_ID);
    }

    public void setAppId(String appId) {
        if (appId == null) alipayParams.remove(Z.APP_ID);
        else alipayParams.put(Z.APP_ID, appId);
    }

    public String getAppenv() {
        return alipayParams.get(Z.APPENV);
    }

    public void setAppenv(String appenv) {
        if (appenv == null) alipayParams.remove(Z.APPENV);
        else alipayParams.put(Z.APPENV, appenv);
    }

    public String getMethod() {
        return alipayParams.get(Z.METHOD);
    }

    public void setMethod(String method) {
        if (method == null) alipayParams.remove(Z.METHOD);
        else alipayParams.put(Z.METHOD, method);
    }

    public String getTimeStamp() {
        return alipayParams.get(Z.TIMESTAMP);
    }

    public void setTimeStamp(String timeStamp) {
        if (timeStamp == null) alipayParams.remove(Z.TIMESTAMP);
        else alipayParams.put(Z.TIMESTAMP, timeStamp);
    }

    public String getVersion() {
        return alipayParams.get(Z.VERSION);
    }

    public void setVersion(String version) {
        if (version == null) alipayParams.remove(Z.VERSION);
        else alipayParams.put(Z.VERSION, version);
    }

    public String getBizContentObj() {
        return alipayParams.get(Z.BIZ_CONTENT);
    }

    public void setBizContentObj(String bizContentObj) {
        if (bizContentObj == null) alipayParams.remove(Z.BIZ_CONTENT);
        else alipayParams.put(Z.BIZ_CONTENT, bizContentObj);
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
        //Log.i("CURRENCY", (String) params.get("CURRENCY"));
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
            //Log.i("WallApi request", orderInfo);
            String sign = sha256(orderInfo);
            parametersMap.put("sign", sign);
//            parametersMap.remove("ps_name");
        } else {
            parametersMap.put("sign", getPwSign());
        }
//        //Log.i("ORDER_INFO", orderInfo);
//        //Log.i("SIGN", sign);

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
        boolean isDomestic = false;
        if (map.containsKey(Z.BIZ_CONTENT)) {
            isDomestic = true;
        }
        List<String> keys = new ArrayList<>(map.keySet());
        StringBuilder sb = new StringBuilder();
        if (isDomestic) {
            try {
                String sign = "";
                for (int i = 0; i < keys.size(); i++) {
                    String key = keys.get(i);
                    if (key.equalsIgnoreCase(Z.SIGN)) {
                        sign = map.get(key);
                    } else {
//                    sb.append(buildKeyValue(key, value, true));
                        sb.append(key);
                        sb.append("=");
                        sb.append(URLEncoder.encode(map.get(key), "UTF-8"));
                        sb.append("&");
                    }
                }
                sb.append(Z.SIGN);
                sb.append("=");
                sb.append(URLEncoder.encode(sign, "UTF-8"));
                return sb.toString();
            } catch (UnsupportedEncodingException e) {
                return null;
            }
            /*String tailKey = keys.get(keys.size() - 1);
            String tailValue = map.get(tailKey);
            sb.append(buildKeyValue(tailKey, tailValue, true));*/
        } else {
            String sign = "";
            String signType = "";
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                if (key.equalsIgnoreCase(Z.SIGN)) {
                    sign = map.get(key);
                } else if (key.equalsIgnoreCase(Z.SIGN_TYPE)) {
                    signType = map.get(key);
                } else {
                    /*String value = "\""+map.get(key)+"\"";
                    sb.append(buildKeyValue(key, value, true));
                    sb.append("&");*/
                    try {
                        sb.append(key);
                        sb.append("=");
                        sb.append("\"");
                        sb.append(map.get(key));
                        sb.append("\"");
                        sb.append("&");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                sb.append(Z.SIGN);
                sb.append("=");
                sb.append("\"");
                sb.append(URLEncoder.encode(sign, "UTF-8"));
                sb.append("\"&");
                sb.append(Z.SIGN_TYPE);
                sb.append("=");
                sb.append("\"");
                sb.append(signType);
                sb.append("\"");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sb.toString();
        }


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

        //Log.i("SIGN_STRING", authInfo.toString());

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
//        dest.writeString(this.partnerId);
//        dest.writeString(this.sellerId);
//        dest.writeString(this.service);
//        dest.writeString(this.outTradeNo);
//        dest.writeString(this.inputCharset);
//        dest.writeString(this.currencyCode);
//        dest.writeString(this.signType);
//        dest.writeString(this.notifyUrl);
//        dest.writeString(this.subject);
//        dest.writeString(this.paymentType);
//        dest.writeString(this.totalFee);
//        dest.writeString(this.body);
//        dest.writeString(this.signature);
//        dest.writeString(this.itbPay);
//        dest.writeString(this.forexBiz);
//        dest.writeString(this.appId);
//        dest.writeString(this.appenv);
//        dest.writeString(this.timeStamp);
//        dest.writeString(this.version);
//        dest.writeString(this.method);
        dest.writeString(this.privateKey);
        dest.writeString(this.pwSign);
        dest.writeInt(this.alipayParams.size());
        for (Map.Entry<String, String> entry : this.alipayParams.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
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
        /*this.partnerId = in.readString();
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
        this.timeStamp = in.readString();
        this.version = in.readString();
        this.method = in.readString();*/
        this.privateKey = in.readString();
        this.pwSign = in.readString();

        int paramsSize = in.readInt();
        this.alipayParams = new TreeMap<String, String>();
        for (int i = 0; i < paramsSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.alipayParams.put(key, value);
        }
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

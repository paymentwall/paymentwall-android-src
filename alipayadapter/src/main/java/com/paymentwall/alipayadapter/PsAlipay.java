package com.paymentwall.alipayadapter;

import android.os.Bundle;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by nguyen.anh on 9/6/2016.
 */

public class PsAlipay implements Serializable {

    private String partnerId;
    private String sellerId;
    private String service;
    private String outTradeNo;
    private String inputCharset;
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
    private Map<String, Object> bundle;


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

    public Map<String, Object> getBundle() {
        return bundle;
    }

    public void setBundle(Map<String, Object> bundle) {
        this.bundle = bundle;
    }

    public boolean isTestMode(){
        return (bundle.get("TEST_MODE") + "").equalsIgnoreCase("true");
    }

    public String getPwSign() {
        return pwSign;
    }

    public void setPwSign(String pwSign) {
        this.pwSign = pwSign;
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

        if(getPwSign() == null) {
            parametersMap.put("ps_name", "alipay");
            String orderInfo = printWallApiMap(sortMap(parametersMap));
            orderInfo += bundle.get("PW_PROJECT_SECRET");
            String sign = sha256(orderInfo);
            parametersMap.put("sign", sign);
            parametersMap.remove("ps_name");
        }else{
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

}

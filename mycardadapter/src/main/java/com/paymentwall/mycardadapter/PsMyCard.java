package com.paymentwall.mycardadapter;

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
 * Created by nguyen.anh on 5/15/2017.
 */

public class PsMyCard implements Serializable {

    private String pwSign;
    private Map<String, Object> bundle;
    private Map<String, String> customParams;

    public Map<String, Object> getBundle() {
        return bundle;
    }

    public void setBundle(Map<String, Object> bundle) {
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

        String orderInfo = printWallApiMap(sortMap(parametersMap));
        orderInfo += bundle.get("PW_PROJECT_SECRET");
        String sign = "";
//        if (((String)bundle.get("SIGN_VERSION")).equalsIgnoreCase("3"))
//            sign = sha256(orderInfo);
//        else if(((String)bundle.get("SIGN_VERSION")).equalsIgnoreCase("2"))
        sign = sha256(orderInfo);
        parametersMap.put("sign", sign);
        Log.i("ORDER_INFO", orderInfo);
        Log.i("SIGN", sign);

        return parametersMap;
    }

    public Map<String, String> makeTransactionProcessingMap(Map<String, String> parametersMap) {

        String orderInfo = printWallApiMap(sortMap(parametersMap));
        orderInfo += bundle.get("PW_PROJECT_SECRET");
//        try {
//            orderInfo = URLEncoder.encode(orderInfo, "UTF-8");
//        }catch(UnsupportedEncodingException e){
//            e.printStackTrace();
//        }
        String sign = "";
        if (parametersMap.get("sign_version").equalsIgnoreCase("3"))
            sign = sha256(orderInfo);
        else if (parametersMap.get("sign_version").equalsIgnoreCase("2"))
            sign = md5(orderInfo);
        parametersMap.put("sign", sign);
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

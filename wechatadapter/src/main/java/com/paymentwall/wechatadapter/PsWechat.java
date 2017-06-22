package com.paymentwall.wechatadapter;

import android.util.Log;

import com.paymentwall.wechatadapter.utils.MD5;
import com.paymentwall.wechatadapter.utils.MD5Utils;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by nguyen.anh on 10/26/2016.
 */

public class PsWechat implements Serializable {

    private String appId, secret, merchantId, prepayId, nonceStr, body, outTradeNo, totalFee, createIp, notifyUrl, tradeType, timeStamp, packageValue, signature;
    private Map<String, Object> bundle;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getSignature() {
        return getPaymentSign();
    }

    public void setSignature(String signature) {
        this.signature = signature;
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

    public Map<String, String> getWallApiParameterMap() {
        TreeMap<String, String> parametersMap = new TreeMap<String, String>();
        parametersMap.put("uid", (String)bundle.get("USER_ID"));
        parametersMap.put("key", (String)bundle.get("PW_PROJECT_KEY"));
        parametersMap.put("ag_name", (String)bundle.get("ITEM_NAME"));
        parametersMap.put("ag_external_id", (String)bundle.get("ITEM_ID"));
        parametersMap.put("amount", bundle.get("AMOUNT") + "");
        parametersMap.put("currencyCode", (String)bundle.get("CURRENCY"));
        parametersMap.put("sign_version", bundle.get("SIGN_VERSION") + "");


        parametersMap.put("ps_name", "wechatpayments");


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

    public String getPrepaidParams() {

        Map<String, Object> orderMap = new HashMap<String, Object>();
        orderMap.put("appid", getAppId());
        orderMap.put("mch_id", getMerchantId());
        orderMap.put("nonce_str", getNonceStr());
        orderMap.put("body", getBody());
        orderMap.put("out_trade_no", getOutTradeNo());
        orderMap.put("total_fee", getTotalFee());
        orderMap.put("spbill_create_ip", getCreateIp());
        orderMap.put("notify_url", getNotifyUrl());
        orderMap.put("trade_type", getTradeType());
        orderMap.put("sign", getPrepaidRequestSign(getAppId(), getSecret(), getMerchantId(), getNonceStr(), getBody(), getOutTradeNo(), getTotalFee(), getCreateIp(), getNotifyUrl(), getTradeType()));

        Set<String> keys = orderMap.keySet();
        for (String key : keys) {
            Log.i("ORDER_MAP", key + " - " + orderMap.get(key));
        }

        String temp = Utils.buildMapToXMLBody(orderMap);
        return temp;

    }

    public static String getPrepaidRequestSign(String APP_ID, String secret, String MCH_ID, String nonce_str, String body, String out_trade_no, String total_fee, String spbill_create_ip, String notify_url, String trade_type) {
        String[] unSign = {"appid=" + APP_ID,
                "mch_id=" + MCH_ID,
                "nonce_str=" + nonce_str,
                "body=" + body,
                "out_trade_no=" + out_trade_no,
                "total_fee=" + total_fee,
                "spbill_create_ip=" + spbill_create_ip,
                "notify_url=" + notify_url,
                "trade_type=" + trade_type};

        unSign = MD5Utils.getInstance().getSortedStr(unSign);
        String temp = "";
        for (int i = 0; i < unSign.length; i++) {
            temp += unSign[i] + "&";
        }

        temp = temp + "key=" + secret;
        Log.v("prepaid_BaseString", temp);

        temp = MD5.getInstance().getMD5(temp).toUpperCase();

        return temp;
    }

    public  String getPaymentSign(){
        String[] unSign =     {"appid="  + getAppId(),
                "noncestr=" + getNonceStr(),
                "package=" + getPackageValue(),
                "partnerid=" + getMerchantId(),
                "prepayid=" + getPrepayId(),
                "timestamp=" + getTimeStamp()};

        unSign = MD5Utils.getInstance().getSortedStr(unSign);
        String temp = "";
        for (int i= 0; i<unSign.length; i++){
            temp += unSign[i] + "&";
        }

        temp = temp + "key=" + getSecret();
        Log.v("order_BaseString", temp);
        temp = MD5.getInstance().getMD5(temp).toUpperCase();
        setSignature(temp);

        return temp;
    }
}
package com.paymentwall.pwunifiedsdk.mobiamo.core;


import com.paymentwall.pwunifiedsdk.mobiamo.payment.PWSDKResponse;

public class MobiamoResponse extends PWSDKResponse {
    private static final long serialVersionUID = -5431404693006176298L;
    public static final String STATUS_COMPLETED = "completed";
    private int messageStatus;
    private String transactionId;
    private String shortcode;
    private String keyword;
    private String regulatoryText;
    private String sign;
    private int success;
    private String status;
    private float amount;
    private String currencyCode;
    private String message;
    private String price;
    private String productId;
    private String productName;

    private boolean isSendSms;

    public String getMessage() {
        return message;
    }

    public void setMessage(String mMessage) {
        this.message = mMessage;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getRegulatoryText() {
        return regulatoryText;
    }

    public void setRegulatoryText(String regulatoryText) {
        this.regulatoryText = regulatoryText;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public boolean getSendSms() {
        return isSendSms;
    }

    public void setSendSms(boolean value) {
        isSendSms = value;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public boolean isCompleted() {
        if (status != null && status.equals(STATUS_COMPLETED)) {
            return true;
        } else {
            return false;
        }
    }
}

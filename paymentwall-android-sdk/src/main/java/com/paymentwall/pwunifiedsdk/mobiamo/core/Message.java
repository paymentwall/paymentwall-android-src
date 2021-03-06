package com.paymentwall.pwunifiedsdk.mobiamo.core;

/**
 * Created by nguyen.anh on 7/27/2016.
 */

public enum Message {
    NO_INTERNET_CONNECTION,
    USER_DID_NOT_ACCEPT_AND_PAY,
    NO_SIM,
    TRANSMISSION_SUCCESS,
    WAITING,
    TRANSACTION_SUCCESS,
    TRANSACTION_FAIL,
    NO_REQUEST_FOUND,
    PURCHASE,
    CONFIRM,
    ACCEPT_AND_PAY,
    CANCEL,
    SEND_SMS_UNKNOWN,
    SMS_NOT_SEND,
    SEND_SMS_SUCCESS,
    SEND_SMS_FAILED,
    SEND_SMS_RADIO_OFF,
    SEND_SMS_NO_PDU,
    SEND_SMS_NO_SERVICE,
    ERROR_GET_SIM_ISO,
    ERROR_GET_LOCALE,
    ERROR_GET_MCC_MNC,
    ERROR_OPERATOR_NOT_SUPPORT,
    ERROR_GENERAL,
    ERROR_POST,
    ERROR_GET_METHOD,
    ERROR_MSG_NOT_SEND,

    GET_PRICE_POINT_SUCCESS,
    POST_SUCCESS,
    GET_SUCCESS
}

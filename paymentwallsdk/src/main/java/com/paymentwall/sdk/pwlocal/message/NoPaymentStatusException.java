package com.paymentwall.sdk.pwlocal.message;

/**
 * Created by paymentwall on 26/08/15.
 */
public class NoPaymentStatusException extends Exception {
    public NoPaymentStatusException(String detailMessage) {
        super(detailMessage);
    }
}

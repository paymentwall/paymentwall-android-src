package com.paymentwall.sdk.pwlocal.utils;

import com.paymentwall.sdk.pwlocal.message.PaymentStatus;

import java.util.List;

/**
 * Callback interface for PaymentStatus
 **/
public interface PaymentStatusCallback {
    /**
     * Error callback
     **/
    void onError(Exception error);

    /**
     * Multiple Payment Statuses callback
     **/
    void onSuccess(List<PaymentStatus> paymentStatusList);
}

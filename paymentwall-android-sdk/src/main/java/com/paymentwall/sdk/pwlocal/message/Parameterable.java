package com.paymentwall.sdk.pwlocal.message;

import java.util.Map;

/**
 * Created by paymentwall on 12/01/16.
 */
interface Parameterable {
    Map<String, String> toParameters();
}

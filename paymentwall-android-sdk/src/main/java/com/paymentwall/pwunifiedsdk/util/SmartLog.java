/*
 * Name: $RCSfile: SmartLog.java,v $
 * Version: $Revision: 1.1 $
 * Date: $Date: May 27, 2011 3:27:39 PM $
 *
 * Copyright (C) 2011 COMPANY NAME, Inc. All rights reserved.
 */

package com.paymentwall.pwunifiedsdk.util;

import android.util.Log;

import com.paymentwall.pwunifiedsdk.BuildConfig;


/**
 * Smart Log
 *
 * @author nguyen.anh
 */
public final class SmartLog {

    private static String TAG = "PaymentwallSdk";

    private SmartLog() {
    }

    /**
     * Call SmartLog.d
     *
     * @param msg
     */
    public static void d(String msg) {
        if (isDebugMode())
            Log.d(TAG, msg);

    }

    /**
     * Call SmartLog.e
     *
     * @param msg
     */
    public static void e(String msg) {
        if (isDebugMode())
            Log.e(TAG, msg);

    }


    /**
     * Call SmartLog.i
     *
     * @param msg
     */
    public static void i(String msg) {
        if (isDebugMode())
            Log.i(TAG, msg);

    }

    public static void longString(String TAG, String str) {
        if (str.length() > 4000) {
            Log.i(TAG, str.substring(0, 4000));
            longString(TAG, str.substring(4000));
        } else
            Log.i(TAG, str);
    }

    public static boolean isDebugMode() {
//        return BuildConfig.DEBUG;
        return true;
    }

}

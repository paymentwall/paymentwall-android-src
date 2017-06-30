package com.paymentwall.cardio;

import android.content.Context;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/**
 * Created by harvey on 5/16/17.
 */

public class PwUtils {
    public static float dpToPx(Context context, float dpValue) {
        float dpFactor = context.getResources().getDisplayMetrics().densityDpi / 160f;
        if (context.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            return dpValue * dpFactor * 0.95f;
        } else {
            return dpValue * dpFactor;
        }
    }
}

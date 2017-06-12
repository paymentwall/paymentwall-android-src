package com.paymentwall.pwunifiedsdk.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.EditText;
import android.widget.TextView;

import com.paymentwall.pwunifiedsdk.brick.utils.Const;

import java.util.HashMap;
import java.util.Map;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/**
 * Created by nguyen.anh on 7/15/2016.
 */

public class PwUtils {

    private static Typeface fontBold;
    private static Typeface fontRegular;
    private static Typeface fontLight;

    public static Typeface getFontBold(Context context) {
        if (fontBold == null)
            fontBold = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Bold.otf");
        return fontBold;
    }

    public static Typeface getFontRegular(Context context) {
        if (fontRegular == null)
            fontRegular = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Regular.otf");
        return fontRegular;
    }

    public static Typeface getFontLight(Context context) {
        if (fontLight == null)
            fontLight = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Light.otf");
        return fontLight;
    }

    public static void setFontRegular(Context context, TextView... textViews) {
        Typeface regularFont = getFontRegular(context);
        for (TextView tv : textViews) {
            if (tv != null)
                tv.setTypeface(regularFont);
        }
    }

    public static void setFontBold(Context context, TextView... textViews) {
        Typeface regularFont = getFontBold(context);
        for (TextView tv : textViews) {
            if (tv != null)
                tv.setTypeface(regularFont);
        }
    }

    public static void setFontLight(Context context, TextView... textViews) {
        Typeface lightFont = getFontLight(context);
        for (TextView tv : textViews) {
            if (tv != null)
                tv.setTypeface(lightFont);
        }
    }

    public static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    public static String getCurrencySymbol(String currencyCode) {
        String symbol = CURRENCY_MAP.get(currencyCode);
        if (symbol != null)
            return symbol;
        return currencyCode;
    }

    public static float dpToPx(Context context, float dpValue) {
        float dpFactor = context.getResources().getDisplayMetrics().densityDpi / 160f;
        if (context.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            return dpValue * dpFactor * 0.95f;
        } else {
            return dpValue * dpFactor;
        }
    }

    public static void logFabricCustom(String event) {

//        try{
//            Answers.getInstance().logCustom(new CustomEvent(event));
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    }

    public static int getLayoutId(Context context, String layoutName) {
        try {
            return context.getResources().getIdentifier(SharedPreferenceManager.getInstance(context).getUIStyle() + "_" + layoutName, "layout", context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return context.getResources().getIdentifier("saas_" + layoutName, "layout", context.getPackageName());
        }
    }

    public static final Map<String, String> CURRENCY_MAP = new HashMap<String, String>() {
        {
            put("ALL", "Lek");
            put("AFN", "؋");
            put("ARS", "$");
            put("AWG", "ƒ");
            put("AUD", "$");
            put("AZN", "ман");
            put("BSD", "$");
            put("BBD", "$");
            put("BYR", "p.");
            put("BZD", "BZ$");
            put("BMD", "$");
            put("BOB", "$b");
            put("BAM", "KM");
            put("BWP", "P");
            put("BGN", "лв");
            put("BRL", "R$");
            put("BND", "$");
            put("KHR", "៛");
            put("CAD", "$");
            put("KYD", "$");
            put("CLP", "$");
            put("CNY", "¥");
            put("COP", "$");
            put("CRC", "₡");
            put("HRK", "kn");
            put("CUP", "₱");
            put("CZK", "Kč");
            put("DKK", "kr");
            put("DOP", "RD$");
            put("XCD", "$");
            put("EGP", "£");
            put("SVC", "$");
            put("EEK", "kr");
            put("EUR", "€");
            put("FKP", "£");
            put("FJD", "$");

            put("GHC", "¢");
            put("GIP", "£");
            put("GTQ", "Q");
            put("GGP", "£");
            put("GYD", "$");
            put("HNL", "L");
            put("HKD", "$");
            put("HUF", "Ft");
            put("ISK", "kr");
            put("INR", "₹");
            put("IDR", "Rp");
            put("IRR", "﷼");
            put("IMP", "£");
            put("ILS", "₪");
            put("JMD", "J$");
            put("JPY", "¥");
            put("JEP", "£");
            put("KZT", "лв");
            put("KPW", "₩");
            put("KRW", "₩");
            put("KGS", "лв");
            put("LAK", "₭");
            put("LVL", "Ls");
            put("LBP", "£");
            put("LRD", "$");
            put("LTL", "Lt");
            put("MKD", "ден");
            put("MYR", "RM");
            put("MUR", "₨");
            put("MXN", "$");
            put("MNT", "₮");
            put("MZN", "MT");
            put("NAD", "$");
            put("NPR", "₨");
            put("ANG", "ƒ");
            put("NZD", "$");
            put("NIO", "C$");
            put("NGN", "₦");
            put("NOK", "kr");
            put("OMR", "﷼");
            put("PKR", "₨");
            put("PAB", "B/.");
            put("PYG", "Gs");
            put("PEN", "S/.");
            put("PHP", "₱");
            put("PLN", "zł");
            put("QAR", "﷼");
            put("RON", "lei");
            put("RUB", "руб");

            put("SHP", "£");
            put("SAR", "﷼");
            put("RSD", "Дин.");
            put("SCR", "₨");
            put("SGD", "$");
            put("SBD", "$");
            put("SOS", "S");
            put("ZAR", "S");
            put("LKR", "₨");
            put("SEK", "kr");
            put("CHF", "CHF");
            put("SRD", "$");
            put("SYP", "£");
            put("TWD", "NT$");
            put("THB", "฿");
            put("TTD", "TT$");
            put("TRL", "₤");
            put("TVD", "$");
            put("UAH", "₴");
            put("GBP", "£");
            put("USD", "$");
            put("UYU", "$U");
            put("UZS", "лв");
            put("VEF", "Bs");
            put("VND", "₫");
            put("YER", "﷼");
            put("ZWD", "Z$");

        }
    };

}

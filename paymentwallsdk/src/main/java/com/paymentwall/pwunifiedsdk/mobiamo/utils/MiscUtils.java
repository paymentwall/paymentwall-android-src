package com.paymentwall.pwunifiedsdk.mobiamo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Currency;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MiscUtils {
	public static String md5(String inputString) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(inputString.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}

	public static String sha256(String inputString) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] array = md.digest(inputString.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}

	public static String urlStringEncode(String input) {
		try {
			return URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static Map<String, String> sortMap(Map<String, String> aMap) {
		Map<String, String> sortedMap = new TreeMap<String, String>(aMap);
		return sortedMap;
	}

	public static String printMap(Map<String, String> params) {
		String out = "";
		for (Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			// System.out.println(key+"="+value);
			out = out + "|" + key + "=" + value;
		}
		return out;
	}

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Generate a value suitable for use in {@link #setId(int)}. This value will
	 * not collide with ID values generated at build time by aapt for R.id.
	 * 
	 * @return a generated ID value
	 */
	public static int generateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range
			// under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	public static boolean isValidCurrency(String currency) {
		try {
			Currency.getInstance(currency);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

    public static byte[] decodeBase64(String input){
        return Base64.decode(input, Base64.DEFAULT);
    }
}

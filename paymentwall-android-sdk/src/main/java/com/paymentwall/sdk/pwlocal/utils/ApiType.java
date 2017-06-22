package com.paymentwall.sdk.pwlocal.utils;

public final class ApiType {
	/**
	* Please use ApiType.VIRTUAL_CURRENCY instead
	**/
	@Deprecated
	public static final String PS = "ps";

	/**
	 * Please use ApiType.DIGITAL_GOODS instead
	 **/
	@Deprecated
	public static final String SUBSCRIPTION = "subscription";
	public static final String CART = "cart";
	public static final String VIRTUAL_CURRENCY = "ps";
	public static final String DIGITAL_GOODS = "subscription";

}

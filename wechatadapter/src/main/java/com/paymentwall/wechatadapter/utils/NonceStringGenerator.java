package com.paymentwall.wechatadapter.utils;

import java.util.Random;

/**
 * Created by Liang on 10/24/16.
 */
public class NonceStringGenerator {

    private static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static NonceStringGenerator instance;

    public static NonceStringGenerator getInstance(){
        if (instance==null){
            instance = new NonceStringGenerator();
        }
        return instance;
    }
    /**
     * To generate a Random nonce string
     * @param length length of str
     * @return
     */
    public String getNONCE_STR(int length){
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i ++){
            stringBuffer.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return stringBuffer.toString();
    }
}

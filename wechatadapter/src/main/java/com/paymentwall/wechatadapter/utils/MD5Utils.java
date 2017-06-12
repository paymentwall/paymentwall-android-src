package com.paymentwall.wechatadapter.utils;

/**
 * Created by Liang on 9/29/16.
 */
public class MD5Utils {

    private static MD5Utils instance;

    public MD5Utils(){}

    public static MD5Utils getInstance(){
        if (instance == null){
            instance = new MD5Utils();
        }
        return instance;
    }

    public String[] getSortedStr(String[] strs){
        QuickSort.quickSort(strs);
        return strs;
    }

}

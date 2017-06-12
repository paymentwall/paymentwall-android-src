package com.paymentwall.wechatadapter.utils;

/**
 * Created by Liang on 10/25/16.
 */
public class QuickSort {

    public static void quickSort(String[] array){
        if(array != null){
            quickSort(array, 0, array.length-1);
        }
    }

    private static void quickSort(String[] array,int beg,int end){
        if (beg < end) {
            int i = beg, j = end;
            String begstr = array[beg];
            while (i < j) {
                while (i < j && array[j].compareToIgnoreCase(begstr) >= 0)
                    j--;
                if (i < j)
                    array[i++] = array[j];

                while (i < j && array[i].compareToIgnoreCase(begstr) < 0)
                    i++;
                if (i < j)
                    array[j--] = array[i];
            }
            array[i] = begstr;
            quickSort(array, beg, i - 1);
            quickSort(array, i + 1, end);
        }
    }
}

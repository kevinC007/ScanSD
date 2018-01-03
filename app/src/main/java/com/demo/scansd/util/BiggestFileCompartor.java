package com.demo.scansd.util;

import com.demo.scansd.aidl.BiggestFile;

import java.util.Comparator;

/**
 * Created by Scott on 2016/5/25.
 */
public class BiggestFileCompartor implements Comparator {
    @Override
    public int compare(Object o1, Object o2){
        BiggestFile f1 = (BiggestFile)o1;
        BiggestFile f2 = (BiggestFile)o2;

        return (int)(f2.getFileLength() - f1.getFileLength());

    }
}


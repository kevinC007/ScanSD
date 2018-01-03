package com.demo.scansd.util;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Scott on 2016/5/25.
 */
public class ExtensionCompartor implements Comparator {
    @Override
    public int compare(Object o1, Object o2){
        Map.Entry<String, Integer> m1 = (Map.Entry<String, Integer>)o1;
        Map.Entry<String, Integer> m2 = (Map.Entry<String, Integer>)o2;

        return m2.getValue() - m1.getValue();

    }
}


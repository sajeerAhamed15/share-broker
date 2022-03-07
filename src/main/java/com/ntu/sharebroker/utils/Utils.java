package com.ntu.sharebroker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String getCurrentDate() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}

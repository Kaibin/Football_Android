package com.orange.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class StringUtil {

    public static Date dateFromStringByFormat(String dateString, String format) {
        if (dateString == null || dateString.length() == 0 || format == null)
            return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0800"));
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public static boolean isEmpty(String str) {
        if (str == null || str.length() ==  0) {
            return true;
        }
        else {
            return false;
        }
    }
    
}

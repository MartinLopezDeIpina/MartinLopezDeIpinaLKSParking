package com.lksnext.parking.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;

public class DateUtils {

    public static int compareStringDates(String d1, String d2){
        Date date1 = parseStringDate(d1);
        Date date2 = parseStringDate(d2);

        if(date1 == null || date2 == null){
            return 0;
        }
        return date1.compareTo(date2);
    }

    public static Date parseStringDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}

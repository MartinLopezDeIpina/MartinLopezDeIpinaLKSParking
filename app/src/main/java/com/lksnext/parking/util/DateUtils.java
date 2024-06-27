package com.lksnext.parking.util;

import com.lksnext.parking.domain.DiaSemana;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static int compareStringDates(String d1, String d2){
        Date date1 = parseStringDate(d1);
        Date date2 = parseStringDate(d2);

        if(date1 == null || date2 == null){
            return 0;
        }
        return date1.compareTo(date2);
    }

    public static int compareStringHours(String h1, String h2){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date date1 = sdf.parse(h1);
            Date date2 = sdf.parse(h2);
            return date1.compareTo(date2);
        } catch (ParseException e) {
            return 0;
        }
    }

    public static String[] getOrderedHours(String hour1, String hour2){
        String horaInicio;
        String horaFin;
        if(DateUtils.compareStringHours(hour1, hour2) >= 0){
            horaInicio = hour2;
            horaFin = hour1;
        }else{
            horaInicio = hour1;
            horaFin = hour2;
        }
        return new String[]{horaInicio, horaFin};
    }

    public static Date parseStringDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String parseStringDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static List<String> getFormatedDays(List<Integer> dayNumbers) {
        List<String> formattedDays = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Integer dayNumber : dayNumbers) {
            if (calendar.get(Calendar.DAY_OF_MONTH) > dayNumber) {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, dayNumber);
            String formattedDay = sdf.format(calendar.getTime());
            formattedDays.add(formattedDay);
            // Reset the calendar month to the current month for the next iteration
            calendar.setTime(new Date());
        }
        return formattedDays;
    }

    public static int getFechaDay(String fecha){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(fecha);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected 'yyyy-MM-dd'.", e);
        }
}

    public static Integer[] getNextSevenDays() {
        Integer[] nextSevenDays = new Integer[7];
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            nextSevenDays[i] = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return nextSevenDays;
    }

    public static String[] getNextSevenDaysInitials() {
        String[] nextSevenDaysInitials = new String[7];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("es", "ES"));

        for (int i = 0; i < 7; i++) {
            String dayOfWeek = sdf.format(calendar.getTime());
            nextSevenDaysInitials[i] = dayOfWeek.substring(0, 1).toUpperCase();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return nextSevenDaysInitials;
    }

    public static boolean horaYaPasada(String hora, String dia) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTimeStr = dia + " " + hora;
        Date horaFinDate;
        try {
            horaFinDate = sdf.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return horaFinDate != null && horaFinDate.getTime() < System.currentTimeMillis();
    }

    public static int getDiaSemanaIndexInCurrentWeek(DiaSemana diaSemana) {
        int diaSemanaOrdinal = diaSemana.ordinal();
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Madrid");
        Calendar calendar = Calendar.getInstance(timeZone);
        int calendarDay = calendar.get(Calendar.DAY_OF_WEEK);
        int currentDayOfWeek = (calendarDay - 2) % 7;
        return ((7 + diaSemanaOrdinal - currentDayOfWeek) % 7) -1;
    }

    public static String getTodayString() {
        return parseStringDate(new Date());
    }

    public static String getNowHourString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }

    public static String getHourFromInteger(Integer hour) {
        return String.format("%02d:00", hour);
    }
}

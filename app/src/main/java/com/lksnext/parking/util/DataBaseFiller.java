package com.lksnext.parking.util;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class DataBaseFiller {
    DataBaseManager db = DataBaseManager.getInstance();
    String userID = "X0gZj6BNaeT6yIVzdN3IWKLyM1S2";

    public void fillDataBase(){
        fillPlazas();
        fillReservas();
    }

    private void fillPlazas(){
        for (int i = 0; i < 50; i++) {
            db.addSpotToDB(new Plaza(i, TipoPlaza.COCHE));
        }
        for (int i = 50; i < 75; i++) {
            db.addSpotToDB(new Plaza(i, TipoPlaza.MOTO));
        }
        for (int i = 75; i < 95; i++) {
            db.addSpotToDB(new Plaza(i, TipoPlaza.ELECTRICO));
        }
        for (int i = 95; i < 100; i++) {
            db.addSpotToDB(new Plaza(i, TipoPlaza.DISCAPACITADO));
        }
    }
    public void fillReservas(){

        List<Task<String>> tasks = new ArrayList<>();
        List<String> reservasParaCompuesta = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            Reserva reserva = new Reserva(String.format("2021-07-0%s", i), userID, 3, new Hora("10:00", "11:00"), true);
            db.addBookingToDB(reserva);
            reservasParaCompuesta.add(reserva.getId());
        }

        db.addReservaCompuestaToDB(userID, reservasParaCompuesta, Long.valueOf(3), new Hora("10:00", "11:00"));

        Reserva reserva = new Reserva("2021-06-01", userID, 57, new Hora("10:30", "11:30"), false);
        Reserva reserva2 = new Reserva("2021-06-01", userID, 78, new Hora("10:00", "12:00"), false);
        Reserva reserva3 = new Reserva("2021-06-01", userID, 98, new Hora("10:00", "17:00"), false);
        db.addBookingToDB(reserva);
        db.addBookingToDB(reserva2);
        db.addBookingToDB(reserva3);
    }

    public void fillPlaza98(){
        Calendar calendar = Calendar.getInstance();
        for(int i = 0; i < 13; i++){
            String date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            Reserva reserva = new Reserva(date, userID, 98, new Hora("00:00", "23:59"), false);
            db.addBookingToDB(reserva);
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Increment the day
        }
    }

    public void fillPlazas75_94(){
        for(int i = 75; i < 95; i++){
            fillPlaza(i);
        }
    }

    public void fillPlazasPruebasHoras(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String date;
        for(int i = 1; i < 9; i++){
            date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            Reserva reserva = new Reserva(date, userID, 98, new Hora(String.format("0%s:00", i), String.format("1%s:00", i)), false);
            db.addBookingToDB(reserva);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        for(int i = 75; i < 95; i++){
            Reserva reserva = new Reserva(date, userID, i, new Hora("10:00", "13:00"), false);
            db.addBookingToDB(reserva);
        }
    }

    public void fillPlaza(int plazaID){
        Calendar calendar = Calendar.getInstance();
        for(int i = 0; i < 13; i++){
            String date = new SimpleDateFormat("yyyy-mm-dd").format(calendar.getTime());
            Reserva reserva = new Reserva(date, userID, plazaID, new Hora("00:00", "23:59"), false);
            db.addBookingToDB(reserva);
            calendar.add(calendar.DAY_OF_MONTH, 1); // increment the day
        }
    }

    public void fillReservasPasadas(){
        for (int i = 0; i < 10; i++) {
            Reserva reserva = new Reserva("2021-06-0" + i, userID, Long.valueOf(3), new Hora("10:00", "11:00"), true, TipoPlaza.COCHE);
            db.addBookingToDB(reserva);
        }
    }

}

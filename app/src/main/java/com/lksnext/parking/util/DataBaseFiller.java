package com.lksnext.parking.util;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class DataBaseFiller {
    DataBaseManager db = DataBaseManager.getInstance();

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
        String userID = "X0gZj6BNaeT6yIVzdN3IWKLyM1S2";

        List<Task<String>> tasks = new ArrayList<>();
        List<String> reservasParaCompuesta = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            Reserva reserva = new Reserva(String.format("2021-07-0%s", i), userID, 3, new Hora(1000, 1100), true);
            db.addBookingToDB(reserva);
            reservasParaCompuesta.add(reserva.getId());
        }

        ReservaCompuesta reservaCompuesta = new ReservaCompuesta(userID, reservasParaCompuesta, 3, new Hora(1000, 1100));
        db.addReservaCompuestaToDB(reservaCompuesta);

        Reserva reserva = new Reserva("2021-06-01", userID, 57, new Hora(1030, 1130), false);
        Reserva reserva2 = new Reserva("2021-06-01", userID, 78, new Hora(1000, 1200), false);
        Reserva reserva3 = new Reserva("2021-06-01", userID, 98, new Hora(1000, 1700), false);
        db.addBookingToDB(reserva);
        db.addBookingToDB(reserva2);
        db.addBookingToDB(reserva3);
    }
}

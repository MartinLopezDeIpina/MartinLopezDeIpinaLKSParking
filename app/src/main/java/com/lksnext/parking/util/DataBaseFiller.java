package com.lksnext.parking.util;

import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.TipoPlaza;

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
        for(int i = 0; i < 3; i++){
            Reserva reserva = new Reserva("2021-06-01", userID, i, new Hora(1000, 1100));
            db.addBookingToDB(reserva);
        }

        Reserva reserva = new Reserva("2021-06-01", userID, 57, new Hora(1030, 1130));
        Reserva reserva2 = new Reserva("2021-06-01", userID, 78, new Hora(1000, 1200));
        Reserva reserva3 = new Reserva("2021-06-01", userID, 98, new Hora(1000, 1700));
        db.addBookingToDB(reserva);
        db.addBookingToDB(reserva2);
        db.addBookingToDB(reserva3);
    }

}

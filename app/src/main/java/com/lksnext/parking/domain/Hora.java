package com.lksnext.parking.domain;
import android.util.Log;

import com.google.firebase.firestore.PropertyName;

public class Hora {

    long horaInicio;
    long horaFin;

    public Hora() {
    }
    public Hora(long horaInicio, long horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    @PropertyName("horaInicio")
    public long getHoraInicio() {
        return horaInicio;
    }

    @PropertyName("horaInicio")
    public void setHoraInicio(long horaInicio) {
        this.horaInicio = horaInicio;
    }

    @PropertyName("horaFin")
    public long getHoraFin() {
        return horaFin;
    }

    @PropertyName("horaFin")
    public void setHoraFin(long horaFin) {
        this.horaFin = horaFin;
    }

    public String getHoraInicioString() {
        String horas = Long.toString(horaInicio).substring(0, 2);
        String minutos = Long.toString(horaInicio).substring(2);
        return String.format("%s:%s", horas, minutos);
    }
    public String getHoraFinString() {
        String horas = Long.toString(horaFin).substring(0, 2);
        String minutos = Long.toString(horaFin).substring(2);
        return String.format("%s:%s", horas, minutos);
    }
}

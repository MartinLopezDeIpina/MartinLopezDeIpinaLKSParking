package com.lksnext.parking.domain;
import android.util.Log;

import com.google.firebase.firestore.PropertyName;

public class Hora {

    String horaInicio;
    String horaFin;

    public Hora() {
    }
    public Hora(String horaInicio, String horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    @PropertyName("horaInicio")
    public String getHoraInicio() {
        return horaInicio;
    }

    @PropertyName("horaInicio")
    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    @PropertyName("horaFin")
    public String getHoraFin() {
        return  horaFin;
    }

    @PropertyName("horaFin")
    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

}

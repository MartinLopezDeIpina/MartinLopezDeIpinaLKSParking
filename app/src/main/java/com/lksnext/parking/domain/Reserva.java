package com.lksnext.parking.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.google.firebase.firestore.PropertyName;

public class Reserva {

    String fecha, usuarioID;

    Integer plazaID;

    Hora hora;
    String reservaID;

    boolean insideReservaMultiple;

    public Reserva() {

    }

    public Reserva(String fecha, String usuarioID, Integer plazaID, Hora hora, Boolean insideReservaMultiple) {
        this.fecha = fecha;
        this.usuarioID = usuarioID;
        this.plazaID = plazaID;
        this.hora = hora;
        this.insideReservaMultiple = insideReservaMultiple;
    }

    public void setReservaID(String reservaID) {
        this.reservaID = reservaID;
    }
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public Integer getPlazaID() {
        return plazaID;
    }

    public void setPlazaID(Integer plazaID) {
        this.plazaID = plazaID;
    }
    public String getReservaID() {
        return reservaID;
    }
    public boolean isInsideReservaMultiple() {
        return insideReservaMultiple;
    }

    public DiaSemana getDiaSemana() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(fecha);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return DiaSemana.values()[dayOfWeek - 1];
        } catch (ParseException e) {
            return null;
        }
    }

    @PropertyName("hora")
    public Hora getHora() {
        return hora;
    }
    @PropertyName("hora")
    public void setHora(Hora hora) {
        this.hora = hora;
    }


    public boolean isCaducada() {
        Date today = getParsedDate();

        if (today == null) {
            return false;
        }

        return today.compareTo(today) >= 0 && hora.getHoraFin() > System.currentTimeMillis();
    }


    private Date getParsedDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(fecha);
        } catch (ParseException e) {
            return null;
        }
    }
}

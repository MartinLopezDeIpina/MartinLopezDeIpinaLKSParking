package com.lksnext.parking.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reserva {

    String fecha, usuario;

    Integer plazaID;

    Hora hora;

    public Reserva() {

    }

    public Reserva(String fecha, String usuarioID, Integer plazaID, Hora hora) {
        this.fecha = fecha;
        this.usuario = usuarioID;
        this.plazaID = plazaID;
        this.hora = hora;
    }


    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getPlazaId() {
        return plazaID;
    }

    public void setPlazaId(Integer plazaID) {
        this.plazaID = plazaID;
    }

    public Hora getHoraInicio() {
        return hora;
    }

    public void setHoraInicio(Hora hora) {
        this.hora = hora;
    }

    public Hora getHoraFin() {
        return hora;
    }

    public void setHoraFin(Hora hora) {
        this.hora = hora;
    }

    }

    public boolean isCaducada(){
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

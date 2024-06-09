package com.lksnext.parking.domain;

import java.util.List;

public class ReservaCompuesta {
    private String usuarioID;
    private List<String> reservasID;
    private Integer plazaID;
    private Hora hora;

    public ReservaCompuesta() {
    }
    public ReservaCompuesta(String usuarioID, List<String> reservasID, Integer plazaID, Hora hora) {
        this.usuarioID = usuarioID;
        this.reservasID = reservasID;
        this.plazaID = plazaID;
        this.hora = hora;
    }

    public String getUsuarioID() {
        return usuarioID;
    }
    public List<String> getReservasID() {
        return reservasID;
    }
    public Integer getPlazaID() {
        return plazaID;
    }
    public Hora getHora() {
        return hora;
    }
}

package com.lksnext.parking.domain;

import java.util.List;

public class ReservaCompuesta {
    private String id;
    private String usuarioID;
    private List<String> reservasID;
    private Long plazaID;
    private Hora hora;

    public ReservaCompuesta() {
    }


    public ReservaCompuesta(String usuarioID, List<String> reservasID, Long plazaID, Hora hora) {
        this.usuarioID = usuarioID;
        this.reservasID = reservasID;
        this.plazaID = plazaID;
        this.hora = hora;
    }
    public ReservaCompuesta(String id, String usuarioID, List<String> reservasID, Long plazaID, Hora hora) {
        this.id = id;
        this.usuarioID = usuarioID;
        this.reservasID = reservasID;
        this.plazaID = plazaID;
        this.hora = hora;
    }
    public ReservaCompuesta(String id, String usuarioID, List<String> reservasID, Integer plazaID, Hora hora) {
        this(id, usuarioID, reservasID, plazaID.longValue(), hora);
    }

    public String getUsuarioID() {
        return usuarioID;
    }
    public List<String> getReservasID() {
        return reservasID;
    }
    public Long getPlazaID() {
        return plazaID;
    }
    public Hora getHora() {
        return hora;
    }

    public String getId() {
        return id;
    }
}

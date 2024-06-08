package com.lksnext.parking.domain;

import java.util.List;

public class ReservaCompuesta {
    private String usuarioID;
    private List<String> reservasID;

    public ReservaCompuesta() {
    }
    public ReservaCompuesta(String usuarioID, List<String> reservasID) {
        this.usuarioID = usuarioID;
        this.reservasID = reservasID;
    }

    public String getUsuarioID() {
        return usuarioID;
    }
    public List<String> getReservasID() {
        return reservasID;
    }
}

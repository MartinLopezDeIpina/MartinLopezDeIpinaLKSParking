package com.lksnext.parking.domain;

public class Notificacion {
    private int requestCode;
    private String reservaID;

    public Notificacion(){}
    public Notificacion(int requestCode, String reservaID) {
        this.requestCode = requestCode;
        this.reservaID = reservaID;
    }

    public int getRequestCode() {
        return requestCode;
    }
    public String getReservaID() {
        return reservaID;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
    public void setReservaID(String reservaID) {
        this.reservaID = reservaID;
    }
}

package com.lksnext.parking.domain;

public class Plaza {

    long id;
    TipoPlaza tipo;

    public Plaza() {

    }

    public Plaza(long id, TipoPlaza tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public TipoPlaza getTipo() {
        return tipo;
    }


    public void setTipo(TipoPlaza tipo) {
        this.tipo = tipo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

package com.lksnext.parking.data;

import com.lksnext.parking.domain.Reserva;

import java.util.List;

public interface ReservasCallback {
    void onCallback(List<Reserva> reservas);
}

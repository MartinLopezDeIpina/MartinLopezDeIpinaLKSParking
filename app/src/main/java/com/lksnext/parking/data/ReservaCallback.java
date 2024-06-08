package com.lksnext.parking.data;

import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;

import java.util.List;

public interface ReservaCallback {
    void onCallback(List<Reserva> reservas, List<ReservaCompuesta> compuestas);
}

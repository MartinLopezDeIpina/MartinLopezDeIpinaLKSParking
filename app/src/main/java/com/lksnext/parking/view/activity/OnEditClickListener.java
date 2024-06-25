package com.lksnext.parking.view.activity;

import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;

import java.util.List;

public interface OnEditClickListener {
    void onEditClick(List<Reserva> reservations, ReservaCompuesta composedReservation);
}

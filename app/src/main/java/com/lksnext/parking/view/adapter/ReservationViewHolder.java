package com.lksnext.parking.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.R;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.TipoPlaza;

public class ReservationViewHolder extends RecyclerView.ViewHolder {
    TextView reservationPlaza;
    TextView reservationDate;
    TextView reservationHour;
    ImageView reservationVehicleImage;

    public ReservationViewHolder(@NonNull View itemView) {
        super(itemView);
        reservationPlaza = itemView.findViewById(R.id.plaza);
        reservationDate = itemView.findViewById(R.id.fecha);
        reservationHour = itemView.findViewById(R.id.hora);
        reservationVehicleImage = itemView.findViewById(R.id.vehiculoIcono);
    }

    public void bind(Reserva reservation) {
        reservationPlaza.setText(String.format("Plaza %s",reservation.getPlazaID()));
        reservationDate.setText(reservation.getFecha());

        String horaInicio = reservation.getHora().getHoraInicio();
        String horaFin = reservation.getHora().getHoraFin();
        reservationHour.setText(String.format("%s - %s", horaInicio, horaFin));

        TipoPlaza tipoPlaza = Parking.getInstance().getTipoPlazaReserva(reservation.getPlazaID());
        switch (tipoPlaza){
            case MOTO:
                reservationVehicleImage.setImageResource(R.drawable.motoicono);
                break;
            case COCHE:
                reservationVehicleImage.setImageResource(R.drawable.cocheicono);
                break;
            case ELECTRICO:
                reservationVehicleImage.setImageResource(R.drawable.electricoicono);
                break;
            case DISCAPACITADO:
                reservationVehicleImage.setImageResource(R.drawable.especialicono);
                break;
        }
    }
}

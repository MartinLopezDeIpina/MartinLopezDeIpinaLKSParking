package com.lksnext.parking.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.R;
import com.lksnext.parking.domain.DiaSemana;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;

import java.util.List;
import java.util.stream.Collectors;

public class ComposedReservationViewHolder extends RecyclerView.ViewHolder{

    TextView reservationPlaza;
    TextView reservationHour;
    ImageView reservationVehicleImage;
    TextView lunes, martes, miercoles, jueves, viernes, sabado, domingo;

    public ComposedReservationViewHolder(@NonNull View itemView) {
        super(itemView);
        reservationPlaza = itemView.findViewById(R.id.plaza);
        reservationHour = itemView.findViewById(R.id.hora);
        reservationVehicleImage = itemView.findViewById(R.id.vehiculoIcono);
        lunes = itemView.findViewById(R.id.lunes);
        martes = itemView.findViewById(R.id.martes);
        miercoles = itemView.findViewById(R.id.miercoles);
        jueves = itemView.findViewById(R.id.jueves);
        viernes = itemView.findViewById(R.id.viernes);
        sabado = itemView.findViewById(R.id.sabado);
        domingo = itemView.findViewById(R.id.domingo);
    }

    public void bind(ReservaCompuesta reservaCompuesta) {

        reservationPlaza.setText(String.format("Plaza %s",reservaCompuesta.getPlazaID()));

        String horaInicio = reservaCompuesta.getHora().getHoraInicio();
        String horaFin = reservaCompuesta.getHora().getHoraFin();
        reservationHour.setText(String.format("%s - %s", horaInicio, horaFin));

        TipoPlaza tipoPlaza = Parking.getInstance().getTipoPlazaReserva(reservaCompuesta.getPlazaID());
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

        List<Reserva> reservas = reservaCompuesta.getReservasID().stream()
                .map(
                    reserva -> {
                        return Parking.getInstance().getReserva(reserva);
                    })
                .collect(Collectors.toList());

        for (Reserva reserva : reservas) {
            DiaSemana diaSemana = reserva.getDiaSemana();
            switch(diaSemana) {
                case LUNES:
                    lunes.setAlpha(1f);
                    break;
                case MARTES:
                    martes.setAlpha(1f);
                    break;
                case MIERCOLES:
                    miercoles.setAlpha(1f);
                    break;
                case JUEVES:
                    jueves.setAlpha(1f);
                    break;
                case VIERNES:
                    viernes.setAlpha(1f);
                    break;
                case SABADO:
                    sabado.setAlpha(1f);
                    break;
                case DOMINGO:
                    domingo.setAlpha(1f);
                    break;
            }

        }

    }
}

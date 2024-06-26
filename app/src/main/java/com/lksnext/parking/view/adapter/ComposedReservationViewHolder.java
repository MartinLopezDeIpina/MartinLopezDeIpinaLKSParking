package com.lksnext.parking.view.adapter;


import androidx.databinding.DataBindingUtil;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.R;
import com.lksnext.parking.databinding.ItemComposedReservationBinding;
import com.lksnext.parking.domain.DiaSemana;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.util.DateUtils;
import com.lksnext.parking.view.activity.OnDeleteClickListener;
import com.lksnext.parking.view.activity.OnEditClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComposedReservationViewHolder extends RecyclerView.ViewHolder{

    TextView reservationPlaza;
    TextView reservationHour;
    ImageView reservationVehicleImage;
    TextView lunes, martes, miercoles, jueves, viernes, sabado, domingo;
    TextView[] dayTextViews;
    ItemComposedReservationBinding binding;
    ImageButton deleteButton;
    ImageButton editButton;


    public ComposedReservationViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
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
        dayTextViews = new TextView[]{
            lunes, martes, miercoles, jueves, viernes, sabado, domingo
        };
        deleteButton = itemView.findViewById(R.id.delete_button);
        editButton = itemView.findViewById(R.id.edit_button_composed);
    }

    public void bind(ReservaCompuesta reservaCompuesta, OnEditClickListener onEditClickListener, OnDeleteClickListener onDeleteClickListener) {
        String[] dayStrings = DateUtils.getNextSevenDaysInitials();
        binding.setDayStrings(dayStrings);

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
            if(reserva != null){
                DiaSemana diaSemana = reserva.getDiaSemana();
                int index = DateUtils.getDiaSemanaIndexInCurrentWeek(diaSemana)+1;
                dayTextViews[index].setAlpha(1.0f);
            }
        }

        deleteButton.setOnClickListener(v -> {
            onDeleteClickListener.onDeleteClick(reservaCompuesta.getId(), true);
        });
        editButton.setOnClickListener(v ->{
            List<Reserva> reservations = new ArrayList<>();
            reservaCompuesta.getReservasID().forEach(reserva -> {
                reservations.add(Parking.getInstance().getReserva(reserva));
            });
            onEditClickListener.onEditClick(reservations, reservaCompuesta);
        });
    }
}

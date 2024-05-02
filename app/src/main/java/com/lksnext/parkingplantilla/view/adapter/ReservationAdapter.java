package com.lksnext.parkingplantilla.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Reserva;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private final List<Reserva> reservations;

    public ReservationAdapter(List<Reserva> reservations) {
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reserva reservation = reservations.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView reservationPlaza;
        TextView reservationDate;
        TextView reservationHour;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            reservationPlaza = itemView.findViewById(R.id.plaza);
            reservationDate = itemView.findViewById(R.id.fecha);
            reservationHour = itemView.findViewById(R.id.hora);
        }

        public void bind(Reserva reservation) {
            reservationPlaza.setText(Long.toString(reservation.getPlazaId().getId()));
            reservationDate.setText(reservation.getFecha().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String horaInicio = sdf.format(new Date(reservation.getHoraInicio().getHoraInicio()));
            String horaFin = sdf.format(new Date(reservation.getHoraFin().getHoraFin()));
            reservationHour.setText(String.format("%s - %s", horaInicio, horaFin));
        }
    }
}
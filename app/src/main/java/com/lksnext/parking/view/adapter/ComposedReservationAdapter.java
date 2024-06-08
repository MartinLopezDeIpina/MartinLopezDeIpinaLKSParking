package com.lksnext.parking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.R;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;


import java.util.List;
import java.util.stream.Collectors;

public class ComposedReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Reserva> reservations;
    private final List<ReservaCompuesta> composedReservations;

    public ComposedReservationAdapter(List<Reserva> reservations, List<ReservaCompuesta> composedReservations) {
        this.reservations = reservations.stream().filter(reserva -> !reserva.isInsideReservaMultiple()).collect(Collectors.toList());
        this.composedReservations = composedReservations;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < reservations.size()) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
            return new ReservationViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_composed_reservation, parent, false);
            return new ComposedReservationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReservationViewHolder) {
            ((ReservationViewHolder) holder).bind(reservations.get(position));
        } else if (holder instanceof ComposedReservationViewHolder) {
            ((ComposedReservationViewHolder) holder).bind(composedReservations.get(position - reservations.size()));
        }
    }

    @Override
    public int getItemCount() {
        return reservations.size() + composedReservations.size();
    }

}
package com.lksnext.parking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.R;
import com.lksnext.parking.domain.Reserva;

import java.util.List;

import com.lksnext.parking.view.activity.OnDeleteClickListener;
import com.lksnext.parking.view.activity.OnEditClickListener;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationViewHolder> {

    protected final List<Reserva> reservations;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public ReservationAdapter(List<Reserva> reservations, OnEditClickListener editListener, OnDeleteClickListener deleteListener) {
        this.reservations = reservations;
        this.onEditClickListener = editListener;
        this.onDeleteClickListener = deleteListener;
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
        holder.bind(reservation, onEditClickListener, onDeleteClickListener);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }
}
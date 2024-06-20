package com.lksnext.parking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.R;

import java.util.ArrayList;
import java.util.List;

public class AvailableSpotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Long> availableSpots;
    
    public AvailableSpotsAdapter(List<Long> availableSpots) {
        this.availableSpots = availableSpots;
    }
    @NonNull
    @Override
    public AvailableSpotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_spot, parent, false);
        return new AvailableSpotsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        long spotID = availableSpots.get(position);
        ((AvailableSpotsViewHolder) holder).bind(spotID);
    }

    @Override
    public int getItemCount() {
        return availableSpots.size();
    }

    static class AvailableSpotsViewHolder extends RecyclerView.ViewHolder {
        TextView spotIDTextView;

        public AvailableSpotsViewHolder(@NonNull View itemView) {
            super(itemView);
            spotIDTextView = itemView.findViewById(R.id.plazaID);
        }

        public void bind(Long plazaID) {
            spotIDTextView.setText(String.format("nº %s", plazaID.toString()));
        }
    }
}

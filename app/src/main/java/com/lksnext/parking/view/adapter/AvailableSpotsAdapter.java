package com.lksnext.parking.view.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AvailableSpotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Integer> availableSpots;
    
    public AvailableSpotsAdapter(List<Integer> availableSpots) {
        this.availableSpots = availableSpots;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

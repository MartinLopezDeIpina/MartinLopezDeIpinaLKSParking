package com.lksnext.parking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.R;
import com.lksnext.parking.viewmodel.BookViewModel;

import java.util.ArrayList;
import java.util.List;

public class AvailableSpotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Long> availableSpots;
    BookViewModel bookViewModel;
    
    public AvailableSpotsAdapter(List<Long> availableSpots, BookViewModel bookViewModel) {
        this.availableSpots = availableSpots;
        this.bookViewModel = bookViewModel;
    }
    @NonNull
    @Override
    public AvailableSpotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_spot, parent, false);
        return new AvailableSpotsViewHolder(view, bookViewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        long spotID = availableSpots.get(position);

        AvailableSpotsViewHolder viewHolder = (AvailableSpotsViewHolder) holder;
        viewHolder.bind(spotID);

        if (bookViewModel.getSelectedSpot().getValue() != null && bookViewModel.getSelectedSpot().getValue().equals(spotID)) {
            viewHolder.itemView.setSelected(true);
        } else {
            viewHolder.itemView.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return availableSpots.size();
    }

    static class AvailableSpotsViewHolder extends RecyclerView.ViewHolder {
        TextView spotIDTextView;
        Long plazaID;


        public AvailableSpotsViewHolder(@NonNull View itemView, BookViewModel bookViewModel) {
            super(itemView);
            spotIDTextView = itemView.findViewById(R.id.plazaID);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long plazaID = Long.parseLong(spotIDTextView.getText().toString().split(" ")[1]);
                    if (bookViewModel.getSelectedSpot().getValue() == null || !bookViewModel.getSelectedSpot().getValue().equals(plazaID)) {
                        bookViewModel.setSelectedSpot(plazaID);
                    } else {
                        bookViewModel.setSelectedSpot(null);
                    }
                }
            });


            bookViewModel.getSelectedSpot().observeForever(selectedSpot -> {
                String text = spotIDTextView.getText().toString();
                if(text.isEmpty()){
                    return;
                }
                Long spotID = Long.parseLong(spotIDTextView.getText().toString().split(" ")[1]);
                if (selectedSpot == null) {
                    itemView.setSelected(false);
                } else {
                    if (spotID.equals(selectedSpot)) {
                        itemView.setSelected(true);
                    }else{
                        itemView.setSelected(false);
                    }
                }
            });
        }

        public void bind(Long plazaID) {
            spotIDTextView.setText(String.format("nº %s", plazaID.toString()));
        }
    }
}

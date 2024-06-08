package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.view.adapter.ReservationAdapter;

import java.util.ArrayList;
import java.util.List;

public class BookContainerFragment extends Fragment {
    private RecyclerView recyclerView;
    private ReservationAdapter adapter;

    public BookContainerFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        com.lksnext.parking.databinding.FragmentBookcontainerBinding binding =
            com.lksnext.parking.databinding.FragmentBookcontainerBinding.inflate(inflater, container, false);

        recyclerView = binding.bookRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Reserva> reservations = new ArrayList<>();
        reservations.add(new Reserva("2021-06-01", "usuario1",  1, new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-08-01", "usuario1",  2, new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-09-01", "usuario1",  3, new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-09-01", "usuario1",  4, new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-09-01", "usuario1",  5, new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-09-01", "usuario1",  6, new Hora(System.currentTimeMillis(), System.currentTimeMillis())));

        adapter = new ReservationAdapter(reservations);
        recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
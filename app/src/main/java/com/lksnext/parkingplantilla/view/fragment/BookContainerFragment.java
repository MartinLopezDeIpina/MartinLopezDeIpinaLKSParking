package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.view.adapter.ReservationAdapter;

import java.util.ArrayList;
import java.util.List;

public class BookContainerFragment extends Fragment {
    private RecyclerView recyclerView;
    private ReservationAdapter adapter;

    public BookContainerFragment() {
        // Es necesario un constructor vacio
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookcontainer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.bookRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Reserva> reservations = new ArrayList<>();
        reservations.add(new Reserva("2021-06-01", "usuario1", "1", new Plaza(3, "a"), new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-08-01", "usuario1", "1", new Plaza(5, "a"), new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-09-01", "usuario1", "1", new Plaza(7, "a"), new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-09-01", "usuario1", "1", new Plaza(7, "a"), new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-09-01", "usuario1", "1", new Plaza(7, "a"), new Hora(System.currentTimeMillis(), System.currentTimeMillis())));
        reservations.add(new Reserva("2021-09-01", "usuario1", "1", new Plaza(7, "a"), new Hora(System.currentTimeMillis(), System.currentTimeMillis())));

        adapter = new ReservationAdapter(reservations);
        recyclerView.setAdapter(adapter);
    }
}
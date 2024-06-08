package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.view.adapter.ComposedReservationAdapter;
import com.lksnext.parking.view.adapter.ReservationAdapter;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class BookActiveHistoryContainerFragment extends Fragment {
    private MainViewModel mainViewModel;
    private RecyclerView recyclerView;
    private ComposedReservationAdapter composedAdapter;

    public BookActiveHistoryContainerFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        com.lksnext.parking.databinding.FragmentBookcontainerBinding binding =
                com.lksnext.parking.databinding.FragmentBookcontainerBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        recyclerView = binding.bookRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mainViewModel.getReservasActivas().observe(getViewLifecycleOwner(), newReservations -> {
            composedAdapter = new ComposedReservationAdapter(newReservations.first, newReservations.second);
            recyclerView.setAdapter(composedAdapter);
        });

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

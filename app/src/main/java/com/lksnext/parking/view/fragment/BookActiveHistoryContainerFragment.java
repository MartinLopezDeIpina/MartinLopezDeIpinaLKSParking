package com.lksnext.parking.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.databinding.FragmentBookcontainerBinding;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.view.activity.OnDeleteClickListener;
import com.lksnext.parking.view.adapter.ComposedReservationAdapter;
import com.lksnext.parking.view.activity.OnEditClickListener;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.util.List;

public class BookActiveHistoryContainerFragment extends Fragment {
    private MainViewModel mainViewModel;
    private RecyclerView recyclerView;
    private ComposedReservationAdapter composedAdapter;
    private ProgressBar progressBar;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public BookActiveHistoryContainerFragment() {
        // Es necesario un constructor vacio
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentBookcontainerBinding binding =
                FragmentBookcontainerBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        progressBar = binding.activeBookingsProgessbar;
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = binding.bookRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LiveData<Pair<List<Reserva>, List<ReservaCompuesta>>> reservasActivas = mainViewModel.getCombinedReservas();

        reservasActivas.observe(getViewLifecycleOwner(), newReservations -> {
            if (newReservations.first.isEmpty() && newReservations.second.isEmpty()) {
                binding.noActiveBookingsIcon.setVisibility(View.VISIBLE);
            }else{
                binding.noActiveBookingsIcon.setVisibility(View.GONE);
            }
            composedAdapter = new ComposedReservationAdapter(newReservations.first, newReservations.second, onEditClickListener, onDeleteClickListener);
            recyclerView.setAdapter(composedAdapter);
            progressBar.setVisibility(View.GONE);
        });


        mainViewModel.getBookingModified().observe(getViewLifecycleOwner(), modifiedReservation -> {
            Lifecycle.State life = getViewLifecycleOwner().getLifecycle().getCurrentState();
            //si se accede después desde un cambio de vista que no se cargue el progressbar
            if(life.equals(Lifecycle.State.RESUMED)){
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onEditClickListener = (OnEditClickListener) context;
        onDeleteClickListener = (OnDeleteClickListener) context;
    }

}

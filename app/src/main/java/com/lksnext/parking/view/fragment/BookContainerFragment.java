package com.lksnext.parking.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.view.activity.OnDeleteClickListener;
import com.lksnext.parking.view.activity.OnEditClickListener;
import com.lksnext.parking.view.adapter.ReservationAdapter;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.util.concurrent.atomic.AtomicBoolean;

public class BookContainerFragment extends Fragment {
    private MainViewModel mainViewModel;
    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout noReservationsLayout;
    private OnEditClickListener onEditClickListener;
    OnDeleteClickListener onDeleteClickListener;

    public BookContainerFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        com.lksnext.parking.databinding.FragmentBookcontainerBinding binding =
            com.lksnext.parking.databinding.FragmentBookcontainerBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        progressBar = binding.activeBookingsProgessbar;
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = binding.bookRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        noReservationsLayout = binding.noActiveBookingsIcon;

        //para que cuando se cambie de fragmento no se cargue el progressbar
        AtomicBoolean isFirstTime = mainViewModel.getIsFirstTime();

        mainViewModel.getReservasActivas().observe(getViewLifecycleOwner(), newReservations -> {
            if(isFirstTime.get()){
                isFirstTime.set(false);
            }else{
                if (newReservations.isEmpty()) {
                    noReservationsLayout.setVisibility(View.VISIBLE);
                } else {
                    noReservationsLayout.setVisibility(View.GONE);
                }
                adapter = new ReservationAdapter(newReservations, onEditClickListener, onDeleteClickListener);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
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
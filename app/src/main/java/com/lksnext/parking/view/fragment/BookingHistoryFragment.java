package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.R;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.view.adapter.ComposedReservationAdapter;
import com.lksnext.parking.view.adapter.PassedBookingsAdapter;
import com.lksnext.parking.view.adapter.ReservationAdapter;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.util.List;

public class BookingHistoryFragment extends Fragment {

    MainViewModel mainViewModel;
    RecyclerView recyclyerViewPassed;
    LinearLayout noActiveBookingsIcon;
    ProgressBar progressBar;

    public BookingHistoryFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        com.lksnext.parking.databinding.FragmentBookingHistoryBinding binding =
                com.lksnext.parking.databinding.FragmentBookingHistoryBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        BookActiveHistoryContainerFragment bookActiveHistoryContainerFragment = new BookActiveHistoryContainerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.commposed_book_container, bookActiveHistoryContainerFragment);
        transaction.commit();


        recyclyerViewPassed = binding.passedBookingsRecyclerView;
        recyclyerViewPassed.setLayoutManager(new LinearLayoutManager(getContext()));

        noActiveBookingsIcon = binding.noPassedBookings;
        progressBar = binding.passedBookingsProgressbar;

        LiveData<List<Reserva>> reservasPasadas = mainViewModel.getReservasPasadas();
        reservasPasadas.observe(getViewLifecycleOwner(), newReservations -> {
            if (newReservations.isEmpty()) {
                binding.noPassedBookings.setVisibility(View.VISIBLE);
            }else{
                binding.noPassedBookings.setVisibility(View.GONE);
            }
            PassedBookingsAdapter adapter = new PassedBookingsAdapter(newReservations);
            recyclyerViewPassed.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        });

        return binding.getRoot();
    }
}

package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parking.R;
import com.lksnext.parking.view.adapter.ComposedReservationAdapter;
import com.lksnext.parking.view.adapter.ReservationAdapter;
import com.lksnext.parking.viewmodel.MainViewModel;

public class BookingHistoryFragment extends Fragment {

    public BookingHistoryFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        com.lksnext.parking.databinding.FragmentBookingHistoryBinding binding =
                com.lksnext.parking.databinding.FragmentBookingHistoryBinding.inflate(inflater, container, false);

        BookActiveHistoryContainerFragment bookActiveHistoryContainerFragment = new BookActiveHistoryContainerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.commposed_book_container, bookActiveHistoryContainerFragment);
        transaction.commit();


        return binding.getRoot();
    }
}

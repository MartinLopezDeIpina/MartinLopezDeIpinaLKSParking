package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parking.databinding.FragmentAddBookingBinding;
import com.lksnext.parking.databinding.FragmentProfileBinding;
import com.lksnext.parking.viewmodel.MainViewModel;

public class AddBookingFragment extends Fragment {
private FragmentAddBookingBinding binding;
    public AddBookingFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBookingBinding.inflate(inflater, container, false);
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return binding.getRoot();
    }
}

package com.lksnext.parking.view.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lksnext.parking.databinding.FragmentProfileBinding;
import com.lksnext.parking.viewmodel.MainViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private MainViewModel mainViewModel;

    public ProfileFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setMainViewModel(mainViewModel);

        bindEstadisticas();

        return binding.getRoot();

    }

    private void bindEstadisticas(){
        Integer[] estadisticas = mainViewModel.getEstadisticasReservas();
        if(estadisticas != null) {
            binding.cocheNum.numberTextView.setText(estadisticas[0].toString());
            binding.motoNum.numberTextView.setText(estadisticas[1].toString());
            binding.electricoNum.numberTextView.setText(estadisticas[2].toString());
            binding.especialNum.numberTextView.setText(estadisticas[3].toString());
            int total = estadisticas[0] + estadisticas[1] + estadisticas[2] + estadisticas[3];
            binding.totalNumberTextView.setText(Integer.toString(total));
        }
    }
}
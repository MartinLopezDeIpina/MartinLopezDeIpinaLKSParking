package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parking.databinding.FragmentConfirmEmailBinding;
import com.lksnext.parking.viewmodel.RegisterViewModel;

public class ConfirmEmailFragmnet extends Fragment {
    private FragmentConfirmEmailBinding binding;
    public ConfirmEmailFragmnet() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = com.lksnext.parking.databinding.FragmentConfirmEmailBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}

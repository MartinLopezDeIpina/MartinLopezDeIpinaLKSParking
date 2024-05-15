package com.lksnext.parkingplantilla.view.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityMainBinding;
import com.lksnext.parkingplantilla.databinding.FragmentMainBinding;
import com.lksnext.parkingplantilla.databinding.FragmentProfileBinding;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;
import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public ProfileFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setMainViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProfileViewModel mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);


    }

}
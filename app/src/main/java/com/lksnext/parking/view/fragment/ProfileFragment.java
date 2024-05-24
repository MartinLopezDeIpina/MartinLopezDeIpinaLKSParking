package com.lksnext.parking.view.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lksnext.parking.R;
import com.lksnext.parking.databinding.ActivityMainBinding;
import com.lksnext.parking.databinding.FragmentMainBinding;
import com.lksnext.parking.databinding.FragmentProfileBinding;
import com.lksnext.parking.viewmodel.MainViewModel;
import com.lksnext.parking.viewmodel.ProfileViewModel;

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
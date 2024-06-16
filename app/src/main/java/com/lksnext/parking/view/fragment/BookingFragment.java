package com.lksnext.parking.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentBookingBinding;
import com.lksnext.parking.viewmodel.MainViewModel;

public class BookingFragment extends Fragment {
    FragmentBookingBinding binding;
    MainViewModel mainViewModel;
    View view;

    public BookingFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        setCurrentFragment();
    }

    private void setCurrentFragment(){
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.flFragment);
        NavController navController = navHostFragment.getNavController();

        if(mainViewModel.getBookingNavigationPosition() == 0){
            navController.navigate(R.id.booking_history);
        }else{
            navController.navigate(R.id.add_booking);
            mainViewModel.setBookingNavigationPosition(0);
        }
    }
}
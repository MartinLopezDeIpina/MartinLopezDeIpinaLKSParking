package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentLoginBinding;
import com.lksnext.parking.databinding.FragmentMainBinding;
import com.lksnext.parking.viewmodel.LoginViewModel;
import com.lksnext.parking.viewmodel.MainViewModel;


public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private MainViewModel mainViewModel;
    private View view;
    public MainFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        BookContainerFragment bookContainerFragment = new BookContainerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.book_container, bookContainerFragment);
        transaction.commit();

        bindAddButton();
        bindViewMoreButton();

        return view;
    }

    public void bindAddButton(){
        binding.addButton.setOnClickListener(v -> {
            mainViewModel.navigateToBookingFragment(true, 1);
        });
    }
    public void bindViewMoreButton(){
        binding.viewMoreButton.setOnClickListener(v -> {
            mainViewModel.navigateToBookingFragment(true, 0);
        });
    }
}

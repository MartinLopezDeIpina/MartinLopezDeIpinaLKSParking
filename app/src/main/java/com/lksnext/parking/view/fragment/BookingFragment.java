package com.lksnext.parking.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentBookingBinding;
import com.lksnext.parking.viewmodel.BookViewModel;
import com.lksnext.parking.viewmodel.MainViewModel;

public class BookingFragment extends Fragment {
    FragmentBookingBinding binding;
    BookViewModel bookViewModel;
    View view;

    private RecyclerView recyclerView, recyclyerViewPassed;

    public BookingFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        if(savedInstanceState != null){
            int whereToNavigate = savedInstanceState.getInt("whereToNavigate");
            bookViewModel.setNavigateToBookingFragment(whereToNavigate);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        bindWhereToNavigate();
    }

    private void bindWhereToNavigate(){
        bookViewModel.getNavigateToBookingFragment().observe(getViewLifecycleOwner(), whereToNavigate -> {
            navigate(whereToNavigate);
        });
    }

    private void navigate(int whereToNavigate){
        if(whereToNavigate == 0 || whereToNavigate == 1){
            Navigation.findNavController(view).navigate(R.id.booking_history);
        }else{
            Navigation.findNavController(view).navigate(R.id.add_booking);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        if(bookViewModel == null){
            return;
        }
        super.onSaveInstanceState(outState);
        outState.putInt("whereToNavigate", bookViewModel.getCurrentFragment());
    }
}
package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentAddBookingBinding;
import com.lksnext.parking.databinding.FragmentProfileBinding;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.viewmodel.BookViewModel;
import com.lksnext.parking.viewmodel.MainViewModel;

public class AddBookingFragment extends Fragment {
private FragmentAddBookingBinding binding;
    private MainViewModel mainViewModel;
    private BookViewModel bookViewModel;
    public AddBookingFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBookingBinding.inflate(inflater, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        bookViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if(isLoading){
                binding.progressBar.setVisibility(View.VISIBLE);
            }else{
                binding.progressBar.setVisibility(View.GONE);
                disableChipsIfNoPlazaAvailable();
            }
        });



        bindReturnButton();


        return binding.getRoot();
    }

    private void bindReturnButton(){
        binding.returning.setOnClickListener(v -> {
           mainViewModel.navigateToMainFragment(true);
        });
    }

    private void disableChipsIfNoPlazaAvailable(){
        if(!bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.COCHE)){
            binding.cocheChip.setEnabled(false);
        }
        if(!bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.MOTO)){
            binding.motoChip.setEnabled(false);
        }
        if(!bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.ELECTRICO)){
            binding.electricChip.setEnabled(false);
        }
        if(!bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.DISCAPACITADO)){
            binding.specialChip.setEnabled(false);
        }
    }
}

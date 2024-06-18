package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

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

        binding.setBookViewModel(bookViewModel);


        disableChipsIfNoPlazaAvailable();

        bindReturnButton();


        return binding.getRoot();
    }

    private void bindReturnButton(){
        binding.returning.setOnClickListener(v -> {
           mainViewModel.navigateToMainFragment(true);
        });
    }

    private void disableChipsIfNoPlazaAvailable() {
        AtomicInteger counter = new AtomicInteger(4); // Initialize counter with the number of LiveData objects

        binding.progressBar.setVisibility(View.VISIBLE); // Show progress bar

        LiveData<Boolean> cocheAvailable = bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.COCHE);
        LiveData<Boolean> motoAvailable = bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.MOTO);
        LiveData<Boolean> electricoAvailable = bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.ELECTRICO);
        LiveData<Boolean> discapacitadoAvailable = bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.DISCAPACITADO);

        cocheAvailable.observe(getViewLifecycleOwner(), available -> {
            if (!available) {
                binding.cocheChip.setEnabled(false);
            }
            if (counter.decrementAndGet() == 0) {
                binding.progressBar.setVisibility(View.GONE); // Hide progress bar when all LiveData objects have updated
            }
        });

        motoAvailable.observe(getViewLifecycleOwner(), available -> {
            if (!available) {
                binding.motoChip.setEnabled(false);
            }
            if (counter.decrementAndGet() == 0) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        electricoAvailable.observe(getViewLifecycleOwner(), available -> {
            if (!available) {
                binding.electricChip.setEnabled(false);
            }
            if (counter.decrementAndGet() == 0) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        discapacitadoAvailable.observe(getViewLifecycleOwner(), available -> {
            if (!available) {
                binding.specialChip.setEnabled(false);
            }
            if (counter.decrementAndGet() == 0) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }
}

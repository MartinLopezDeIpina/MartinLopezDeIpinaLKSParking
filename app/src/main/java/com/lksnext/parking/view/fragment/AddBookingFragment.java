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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        CompletableFuture<Boolean> cocheAvailable = bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.COCHE);
        CompletableFuture<Boolean> motoAvailable = bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.MOTO);
        CompletableFuture<Boolean> electricoAvailable = bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.ELECTRICO);
        CompletableFuture<Boolean> discapacitadoAvailable = bookViewModel.isTipoPlazaDisponibleEstaSemana(TipoPlaza.DISCAPACITADO);

        //Chequar de forma asíncrona si hay plazas disponibles de cada tipo
        CompletableFuture.allOf(cocheAvailable, motoAvailable, electricoAvailable, discapacitadoAvailable).thenRun(() -> {
            try {
                if (!cocheAvailable.get()) {
                    binding.cocheChip.setEnabled(false);
                }
                if (!motoAvailable.get()) {
                    binding.motoChip.setEnabled(false);
                }
                if (!electricoAvailable.get()) {
                    binding.electricChip.setEnabled(false);
                }
                if (!discapacitadoAvailable.get()) {
                    binding.specialChip.setEnabled(false);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }finally {
                getActivity().runOnUiThread(() -> binding.progressBar.setVisibility(View.GONE));
            }
        });
}

}

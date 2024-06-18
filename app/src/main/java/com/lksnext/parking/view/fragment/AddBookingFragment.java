package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentAddBookingBinding;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.viewmodel.BookViewModel;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AddBookingFragment extends Fragment {
private FragmentAddBookingBinding binding;
    private MainViewModel mainViewModel;
    private BookViewModel bookViewModel;
    private Chip[] hourChips;
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

        fillHourChipBindings();

        bindReturnButton();

        bindSelectedItems();

        bindDisableOrEnableHourChips();


        return binding.getRoot();
    }

    private void fillHourChipBindings(){
        hourChips = new Chip[24];
        GridLayout gridLayout = binding.getRoot().findViewById(R.id.hour_chip_grid);

        int count = gridLayout.getChildCount();
        for(int i = 0 ; i <count ; i++){
            View child = gridLayout.getChildAt(i);
            if (child instanceof Chip) {
                hourChips[i] = (Chip) child;
                hourChips[i].setEnabled(false);
            }
        }
    }

    private void bindReturnButton(){
        binding.returning.setOnClickListener(v -> {
           mainViewModel.navigateToMainFragment(true);
        });
    }

    private void bindSelectedItems(){
        bindSelectedTipoPlaza();
        bindSelectedDias();
    }

    private void bindSelectedTipoPlaza(){
        binding.cocheChip.setOnClickListener(v -> {
            bookViewModel.toggleSelectedTipoPlaza(TipoPlaza.COCHE);
        });
        binding.motoChip.setOnClickListener(v -> {
            bookViewModel.toggleSelectedTipoPlaza(TipoPlaza.MOTO);
        });
        binding.electricChip.setOnClickListener(v -> {
            bookViewModel.toggleSelectedTipoPlaza(TipoPlaza.ELECTRICO);
        });
        binding.specialChip.setOnClickListener(v -> {
            bookViewModel.toggleSelectedTipoPlaza(TipoPlaza.DISCAPACITADO);
        });
    }
    private void bindSelectedDias(){
        Chip lunesChip = binding.getRoot().findViewById(R.id.lunes_chip).findViewById(R.id.chip);
        Chip martesChip = binding.getRoot().findViewById(R.id.martes_chip).findViewById(R.id.chip);
        Chip miercolesChip = binding.getRoot().findViewById(R.id.miercoles_chip).findViewById(R.id.chip);
        Chip juevesChip = binding.getRoot().findViewById(R.id.jueves_chip).findViewById(R.id.chip);
        Chip viernesChip = binding.getRoot().findViewById(R.id.viernes_chip).findViewById(R.id.chip);
        Chip sabadoChip = binding.getRoot().findViewById(R.id.sabado_chip).findViewById(R.id.chip);
        Chip domingoChip = binding.getRoot().findViewById(R.id.domingo_chip).findViewById(R.id.chip);

        lunesChip.setOnClickListener(v -> {
            bookViewModel.toggleDia(0);
        });
        martesChip.setOnClickListener(v -> {
            bookViewModel.toggleDia(1);
        });
        miercolesChip.setOnClickListener(v -> {
            bookViewModel.toggleDia(2);
        });
        juevesChip.setOnClickListener(v -> {
            bookViewModel.toggleDia(3);
        });
        viernesChip.setOnClickListener(v -> {
            bookViewModel.toggleDia(4);
        });
        sabadoChip.setOnClickListener(v -> {
            bookViewModel.toggleDia(5);
        });
        domingoChip.setOnClickListener(v -> {
            bookViewModel.toggleDia(6);
        });
    }

    private void bindDisableOrEnableHourChips(){
        bookViewModel.getSelectedTipoPlaza().observe(getViewLifecycleOwner(), tipoPlaza -> {
            if (tipoPlaza == null) {
                disableHourChips();
            }else{
                if(bookViewModel.getSelectedDias().getValue() == null){
                    return;
                }
                if(!bookViewModel.getSelectedDias().getValue().isEmpty()) {
                    enableHourChips();
                }
            }
        });
        bookViewModel.getSelectedDias().observe(getViewLifecycleOwner(), dias -> {
            if (dias == null || dias.isEmpty()) {
                disableHourChips();
            }else{
                if(bookViewModel.getSelectedTipoPlaza().getValue() == null){
                    return;
                }
                enableHourChips();
            }
        });
    }

   private void disableHourChips() {
        for (Chip hourChip : hourChips) {
            hourChip.setEnabled(false);
        }
   }

    private void enableHourChips() {
        TipoPlaza tipoPlaza = bookViewModel.getSelectedTipoPlaza().getValue();

        List<Integer> dias = bookViewModel.getSelectedDias().getValue();
        if (dias == null) {
            return;
        }
        List<String> fechas_dias = dias.stream().map(this::getStringFormatDateFromDayNumber).collect(Collectors.toList());

        for (Chip hourChip : hourChips) {
            LiveData<Boolean> available = bookViewModel.isHoraDisponibleInSelectedSpotTypeAndDays(fechas_dias, hourChip.getText().toString(), tipoPlaza);
            available.observe(getViewLifecycleOwner(), isAvailable -> {
                hourChip.setEnabled(isAvailable);
            });
        }
    }

    public String getStringFormatDateFromDayNumber(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);

        if (calendar.getTime().before(new Date())) {
            calendar.add(Calendar.MONTH, 1);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(calendar.getTime());
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

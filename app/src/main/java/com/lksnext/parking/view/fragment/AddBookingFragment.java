package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentAddBookingBinding;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.view.adapter.AvailableSpotsAdapter;
import com.lksnext.parking.viewmodel.BookViewModel;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AddBookingFragment extends Fragment {
private FragmentAddBookingBinding binding;
    private MainViewModel mainViewModel;
    private BookViewModel bookViewModel;
    private RecyclerView recyclerView;
    private AvailableSpotsAdapter spotsAdapter;
    private ProgressBar hoursProgressBar;
    private ProgressBar spotsProgressBar;
    private MaterialButton addBookingButton;
    private List<Chip> hourChips;
    public AddBookingFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bookViewModel.emptyBooking();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBookingBinding.inflate(inflater, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        binding.setBookViewModel(bookViewModel);

        bindSelectedSpot();
        bindAddBookingButton();
        bindProgressBars();
        bindSpotsRecyclerView();
        fillHourChipBindings();
        bindReturnButton();
        bindSelectedItems();
        bindDisableOrEnableHourChips();
        bindSelectedHourValues();
        bindSelectedHourChips();
        bindUnselectedHourValue();
        bindVisualPath();


        return binding.getRoot();
    }
    private void bindSelectedSpot(){
        bookViewModel.getSelectedSpot().observe(getViewLifecycleOwner(), selectedSpot -> {
            if(selectedSpot != null){

            }
        });
    }
    private void bindAddBookingButton(){
        addBookingButton = binding.addBookingButton;
        addBookingButton.setOnClickListener(v -> {

        });
    }
    private void bindProgressBars(){
        hoursProgressBar = binding.hourChipProgressBar;
        spotsProgressBar = binding.plazaRecyclerProgressBar;
    }
    private void bindSpotsRecyclerView(){
        recyclerView = binding.availableSpotsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookViewModel.getAvailableSpots().observe(getViewLifecycleOwner(), availableSpots -> {
            spotsAdapter = new AvailableSpotsAdapter(availableSpots, bookViewModel);
            recyclerView.setAdapter(spotsAdapter);
            spotsProgressBar.setVisibility(View.GONE);
        });
    }

    private void fillHourChipBindings(){
        hourChips = new ArrayList<>(24);
        GridLayout gridLayout = binding.getRoot().findViewById(R.id.hour_chip_grid);

        int count = gridLayout.getChildCount();
        for(int i = 0 ; i <count ; i++){
            View child = gridLayout.getChildAt(i);
            if (child instanceof Chip) {
                child.setEnabled(false);
                hourChips.add((Chip) child);
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
            unCheckAllHourChips();
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
            unCheckAllHourChips();
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

    private void unCheckAllHourChips(){
        for(Chip chip : hourChips){
            chip.setChecked(false);
        }
        bookViewModel.setIntermediateSelectedHours(new ArrayList<>());
    }

   private void disableHourChips() {
        for (Chip hourChip : hourChips) {
            hourChip.setEnabled(false);
        }
   }

    private void enableHourChips() {
        hoursProgressBar.setVisibility(View.VISIBLE);

        TipoPlaza tipoPlaza = bookViewModel.getSelectedTipoPlaza().getValue();

        List<Integer> dias = bookViewModel.getSelectedDias().getValue();
        if (dias == null) {
            return;
        }
        List<String> fechas_dias = dias.stream().map(this::getStringFormatDateFromDayNumber).collect(Collectors.toList());

        LiveData<HashMap<String, Boolean>> availableHours = bookViewModel.getHorasDisponiblesInSelectedSpotTypeAndDays(fechas_dias, tipoPlaza);
        availableHours.observe(getViewLifecycleOwner(), horasDisponibles -> {
            for(Chip chip : hourChips){
                chip.setEnabled(horasDisponibles.get(chip.getText().toString()));
            }

            hoursProgressBar.setVisibility(View.GONE);
        });
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

    private void bindSelectedHourChips(){
        for(Chip hourChip : hourChips){
            hourChip.setOnClickListener(v -> {
                if(possibleToSelectHourChip(hourChip)){
                    bookViewModel.toggleSelectedHour(hourChip.getText().toString());
                }else{
                    hourChip.setChecked(false);
                }
            });
        }
    }

    private boolean possibleToSelectHourChip(Chip hourChip){

        if(bookViewModel.getSelectedHora1().getValue() == null && bookViewModel.getSelectedHora2().getValue() == null){
            return true;
        }

        String hourToCheck = bookViewModel.getLastSelectedHour();
        if(bookViewModel.getSelectedHora1().getValue() == null){
            hourToCheck = bookViewModel.getSelectedHora2().getValue();
        }
        if(bookViewModel.getSelectedHora2().getValue() == null){
            hourToCheck = bookViewModel.getSelectedHora1().getValue();
        }

        return availablePathBetweenHours(hourToCheck, hourChip.getText().toString());
    }

    private void bindSelectedHourValues(){
        bookViewModel.getSelectedHora1().observe(getViewLifecycleOwner(), hora1 -> {
            hourSelected(hora1);
            if(bookViewModel.bothHoursSelected()){
                spotsProgressBar.setVisibility(View.VISIBLE);
            }
        });
        bookViewModel.getSelectedHora2().observe(getViewLifecycleOwner(), hora2 -> {
            hourSelected(hora2);
            if(bookViewModel.bothHoursSelected()){
                spotsProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void bindUnselectedHourValue(){
        bookViewModel.getUnselectedHora().observe(getViewLifecycleOwner(), unselectedHora -> {
            if(unselectedHora != null){
                hourChips.stream().filter(chip -> chip.getText().toString().equals(unselectedHora)).findFirst().ifPresent(chip -> {
                    chip.setChecked(false);
                });
            }
        });
    }

    private void hourSelected(String hora){
        hourChips.stream().filter(chip -> chip.getText().toString().equals(hora)).findFirst().ifPresent(chip -> {
            if(bookViewModel.bothHoursSelected()){
                tryToSetBookHours();
            }
        });
    }

    private void tryToSetBookHours(){
        String hour1 = bookViewModel.getSelectedHora1().getValue();
        String hour2 = bookViewModel.getSelectedHora2().getValue();

        bookViewModel.setPlazasAvailables(true);
        setVisualPath(hour1, hour2);
    }

    private boolean availablePathBetweenHours(String hour1, String hour2){
        Integer[] indexes = getHoursIndexes(hour1, hour2);
        int hour1Index = indexes[0];
        int hour2Index = indexes[1];

        boolean available = true;
        for(int i = hour1Index; i <= hour2Index; i++){
            Chip chip = hourChips.get(i);
            if(!chip.isEnabled()){
                return false;
            }
        }
        return available;
    }

    private void setVisualPath(String hour1, String hour2){
        Integer[] indexes = getHoursIndexes(hour1, hour2);
        int hour1Index = indexes[0];
        int hour2Index = indexes[1];

        List<String> intermediateChipHours = new ArrayList<>();
        for(int i = hour1Index+1; i < hour2Index; i++){
            Chip chip = hourChips.get(i);
            intermediateChipHours.add(chip.getText().toString());
        }

        bookViewModel.setIntermediateSelectedHours(intermediateChipHours);
    }

    private Integer[] getHoursIndexes(String hour1, String hour2){
        List<String> mappedChips = hourChips.stream().map(Chip::getText).map(CharSequence::toString).collect(Collectors.toList());
        int hour1Index = mappedChips.indexOf(hour1);
        int hour2Index = mappedChips.indexOf(hour2);
        if (hour1Index > hour2Index) {
            int temp = hour1Index;
            hour1Index = hour2Index;
            hour2Index = temp;
        }
        return new Integer[]{hour1Index, hour2Index};
    }

    private void bindVisualPath(){
        bookViewModel.getIntermediateSelectedHours().observe(getViewLifecycleOwner(), intermediateHours -> {
            for (Chip chip : hourChips) {
                String chipText = chip.getText().toString();
                if(intermediateHours.contains(chipText)){
                    chip.setChipBackgroundColorResource(R.color.chip_hour_intermediate);
                }else {
                    chip.setChipBackgroundColorResource(R.color.chip_background_color);
                }
            }
        });
    }


}

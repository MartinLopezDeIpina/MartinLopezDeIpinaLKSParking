package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentAddBookingBinding;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.util.notifications.NotificationsManager;
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

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class AddBookingFragment extends Fragment {
private FragmentAddBookingBinding binding;
    private MainViewModel mainViewModel;
    private BookViewModel bookViewModel;
    private RecyclerView recyclerView;
    private AvailableSpotsAdapter spotsAdapter;
    private ProgressBar hoursProgressBar;
    private ProgressBar spotsProgressBar;
    private ProgressBar generalProgressBar;
    private MaterialButton addBookingButton;
    private Chip lunesChip, martesChip, miercolesChip, juevesChip, viernesChip, sabadoChip, domingoChip;
    private Chip cocheChip, motoChip, electricChip, specialChip;
    private List<Chip> hourChips;
    private LinearLayout noSpotIcon;
    private TextView title;
    public AddBookingFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(bookViewModel != null){

            bookViewModel.setIsEditing(false);

            savedInstanceState.putString("textHeading", title.getText().toString());
            savedInstanceState.putString("textButton", addBookingButton.getText().toString());

            savedInstanceState.putBoolean("carChecked", cocheChip.isChecked());
            savedInstanceState.putBoolean("motoChecked", motoChip.isChecked());
            savedInstanceState.putBoolean("electricChecked", electricChip.isChecked());
            savedInstanceState.putBoolean("specialChecked", specialChip.isChecked());

            savedInstanceState.putBoolean("lunesChecked", lunesChip.isChecked());
            savedInstanceState.putBoolean("martesChecked", martesChip.isChecked());
            savedInstanceState.putBoolean("miercolesChecked", miercolesChip.isChecked());
            savedInstanceState.putBoolean("juevesChecked", juevesChip.isChecked());
            savedInstanceState.putBoolean("viernesChecked", viernesChip.isChecked());
            savedInstanceState.putBoolean("sabadoChecked", sabadoChip.isChecked());
            savedInstanceState.putBoolean("domingoChecked", domingoChip.isChecked());
        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBookingBinding.inflate(inflater, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        binding.setBookViewModel(bookViewModel);

        bookViewModel.setCurrentFragment(2);

        if(savedInstanceState == null){
            bookViewModel.emptyBooking();
        }


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
        bindChangeTitleAndAddButtonTextWhenEditing();
        noSpotIcon = binding.noSpotIcon;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if(savedInstanceState != null){
            if(bookViewModel.getIsEditing()){
                bookViewModel.deleteEditedBookingfromDB();
                mainViewModel.updateReservas();
                title.setText(R.string.edit_booking);
                addBookingButton.setText(R.string.edit_booking);
            }

            title.setText(savedInstanceState.getString("textHeading"));
            addBookingButton.setText(savedInstanceState.getString("textButton"));

            if(savedInstanceState.getBoolean("carChecked")){
                cocheChip.setChecked(true);
            }
            if(savedInstanceState.getBoolean("motoChecked")){
                motoChip.setChecked(true);
            }
            if(savedInstanceState.getBoolean("electricChecked")){
                electricChip.setChecked(true);
            }
            if(savedInstanceState.getBoolean("specialChecked")){
                specialChip.setChecked(true);
            }

            boolean lunesChecked = savedInstanceState.getBoolean("lunesChecked");
            lunesChip.post(new Runnable() {
                @Override
                public void run() {
                    lunesChip.setChecked(lunesChecked);
                }
            });
            boolean martesChecked = savedInstanceState.getBoolean("martesChecked");
            martesChip.post(new Runnable() {
                @Override
                public void run() {
                    martesChip.setChecked(martesChecked);
                }
            });
            boolean miercolesChecked = savedInstanceState.getBoolean("miercolesChecked");
            miercolesChip.post(new Runnable() {
                @Override
                public void run() {
                    miercolesChip.setChecked(miercolesChecked);
                }
            });
            boolean juevesChecked = savedInstanceState.getBoolean("juevesChecked");
            juevesChip.post(new Runnable() {
                @Override
                public void run() {
                    juevesChip.setChecked(juevesChecked);
                }
            });
            boolean viernesChecked = savedInstanceState.getBoolean("viernesChecked");
            viernesChip.post(new Runnable() {
                @Override
                public void run() {
                    viernesChip.setChecked(viernesChecked);
                }
            });
            boolean sabadoChecked = savedInstanceState.getBoolean("sabadoChecked");
            sabadoChip.post(new Runnable() {
                @Override
                public void run() {
                    sabadoChip.setChecked(sabadoChecked);
                }
            });
            boolean domingoChecked = savedInstanceState.getBoolean("domingoChecked");
            domingoChip.post(new Runnable() {
                @Override
                public void run() {
                    domingoChip.setChecked(domingoChecked);
                }
            });
        }

    }


    @Override
    public void onPause(){
        super.onPause();
        bookViewModel.anadirNotificacionesReservaEnEdicion(getContext());
        bookViewModel.addEditingReservationIfEditCancelled();
        bookViewModel.emptyBookingBelowData();
        bookViewModel.setEditingSpotAlreadySet(false);
    }

    private void bindSelectedSpot(){
        bookViewModel.getSelectedSpot().observe(getViewLifecycleOwner(), selectedSpot -> {
            if(selectedSpot != null){
                addBookingButton.setEnabled(true);
                addBookingButton.setAlpha(1f);
            }else{
                addBookingButton.setEnabled(false);
                addBookingButton.setAlpha(0.5f);
            }
        });
    }
    private void bindAddBookingButton(){
        addBookingButton = binding.addBookingButton;
        addBookingButton.setOnClickListener(v -> {
            generalProgressBar.setVisibility(View.VISIBLE);
            LiveData<String> bookingResult = bookViewModel.bookSpot();
            bookingResult.observe(getViewLifecycleOwner(), result -> {
                if(result != null){
                    bookViewModel.setSuccssEditIfEditing();
                    generalProgressBar.setVisibility(View.GONE);

                    LiveData<Boolean> reservasUpdated = mainViewModel.updateReservas();
                    reservasUpdated.observeForever(updated -> {
                        if(updated){
                            bookViewModel.anadirNotificaciones(result, getContext());
                        }
                    });

                    bookViewModel.setNavigateToMainFragment(true);

                    setSuccessToast();
                }
            });
        });
    }

    private void setSuccessToast() {
        Long plazaID = bookViewModel.getSelectedSpot().getValue();
        String toastTitle = "Reserva añadida";
        String toastMessage = String.format("Reserva de plaza %s añadida correctamente", plazaID);
        if(bookViewModel.getIsEditing()){
            toastTitle = "Reserva editada";
            toastMessage = String.format("Reserva de plaza %s editada correctamente", plazaID);
        }


        MotionToast.Companion.createColorToast(
                getActivity(),
                toastTitle,
                toastMessage,
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(getActivity(),R.font.robotobold)
        );
    }

    private void bindProgressBars(){
        hoursProgressBar = binding.hourChipProgressBar;
        spotsProgressBar = binding.plazaRecyclerProgressBar;
        generalProgressBar = binding.generalProgressBar;
    }
    private void bindSpotsRecyclerView(){

        recyclerView = binding.availableSpotsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookViewModel.getAvailableSpots().observe(getViewLifecycleOwner(), availableSpots -> {
            if(availableSpots.isEmpty()){
                noSpotIcon.setVisibility(View.VISIBLE);
            }else{
                noSpotIcon.setVisibility(View.GONE);
            }
            spotsAdapter = new AvailableSpotsAdapter(availableSpots, bookViewModel, bookViewModel.getEditingBookingSpot());
            recyclerView.setAdapter(spotsAdapter);
            spotsProgressBar.setVisibility(View.GONE);
            addBookingButton.setSelected(false);
            addBookingButton.setAlpha(0.5f);

            setEditingSpotIfEditing();
        });

    }

    private void setEditingSpotIfEditing(){
        if(bookViewModel.getIsEditing() && !bookViewModel.isEditingSpotAlreadySet()){
            bookViewModel.setSelectedSpot(bookViewModel.getEditingBookingSpot());
            bookViewModel.setEditingSpotAlreadySet(true);
        }
    }

    private void fillHourChipBindings(){
        hourChips = new ArrayList<>(25);
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
            bookViewModel.setNavigateToMainFragment(true);
        });
    }

    private void bindSelectedItems(){
        bindSelectedTipoPlaza();
        bindSelectedDias();
    }

    private void bindSelectedTipoPlaza(){
        cocheChip = binding.cocheChip;
        motoChip = binding.motoChip;
        electricChip = binding.electricChip;
        specialChip = binding.specialChip;

        cocheChip.setOnClickListener(v -> {
            bookViewModel.toggleSelectedTipoPlaza(TipoPlaza.COCHE);
        });
        motoChip.setOnClickListener(v -> {
            bookViewModel.toggleSelectedTipoPlaza(TipoPlaza.MOTO);
        });
        electricChip.setOnClickListener(v -> {
            bookViewModel.toggleSelectedTipoPlaza(TipoPlaza.ELECTRICO);
        });
        specialChip.setOnClickListener(v -> {
            bookViewModel.toggleSelectedTipoPlaza(TipoPlaza.DISCAPACITADO);
        });

        if(bookViewModel.getIsEditing()){
            TipoPlaza tipoPlaza = bookViewModel.getEditingBookingTipoPlaza();
            switch (tipoPlaza){
                case COCHE:
                    binding.cocheChip.performClick();
                    break;
                case MOTO:
                    binding.motoChip.performClick();
                    break;
                case ELECTRICO:
                    binding.electricChip.performClick();
                    break;
                case DISCAPACITADO:
                    binding.specialChip.performClick();
                    break;
            }
        }
    }
    private void bindSelectedDias(){
        lunesChip = binding.dateChipGroup.lunesChip.chip;
        martesChip = binding.dateChipGroup.martesChip.chip;
        miercolesChip = binding.dateChipGroup.miercolesChip.chip;
        juevesChip = binding.dateChipGroup.juevesChip.chip;
        viernesChip = binding.dateChipGroup.viernesChip.chip;
        sabadoChip = binding.dateChipGroup.sabadoChip.chip;
        domingoChip = binding.dateChipGroup.domingoChip.chip;
        Chip[] diasChips = new Chip[]{lunesChip, martesChip, miercolesChip, juevesChip, viernesChip, sabadoChip, domingoChip};

        for(int i = 0; i < diasChips.length; i++){
            int finalI = i;
            diasChips[i].setOnClickListener(v -> {
                bookViewModel.toggleDia(finalI);
            });
        }

        if(bookViewModel.getIsEditing()){
            List<Integer> dias = bookViewModel.getEditingBookingOffsets();
            if(dias != null){
                for(int dia : dias){
                    diasChips[dia].performClick();
                }
            }
        }

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
                Boolean enabled = horasDisponibles.get(chip.getText().toString());
                if(enabled == null){
                    enabled = false;
                }

                //si la segunda hora está deshabilitada, la primera también
                int chipIndex = hourChips.indexOf(chip);
                if(chipIndex == 1){
                   if(!enabled){
                       hourChips.get(0).setEnabled(false);
                   }
                }
                //para que no se quede una hora habilitada sola
                if(chipIndex != 0 && chipIndex != 1){
                    Chip priorChip = hourChips.get(chipIndex - 1);
                    Chip twoPriorChip = hourChips.get(chipIndex - 2);
                    if(!enabled && !twoPriorChip.isEnabled()){
                        priorChip.setEnabled(false);
                    }
                }
                chip.setEnabled(enabled);
            }
            if(bookViewModel.getIsEditing() && !bookViewModel.isEditingHoursAlredySet()){
                bookViewModel.setEditingHoursAlredySet(false);
                setEditingHours();
            }

            hoursProgressBar.setVisibility(View.GONE);
        });
    }

    private void setEditingHours(){
        String hora1 = bookViewModel.getEditingBookingHora1();
        String hora2 = bookViewModel.getEditingBookingHora2();

        for(Chip hourChip : hourChips){
            if(hourChip.getText().toString().equals(hora1) || hourChip.getText().toString().equals(hora2)){
                hourChip.performClick();
            }
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
            bookViewModel.setSelectedSpot(null);
            hourSelected(hora1);
            if(bookViewModel.bothHoursSelected()){
                spotsProgressBar.setVisibility(View.VISIBLE);
            }
        });
        bookViewModel.getSelectedHora2().observe(getViewLifecycleOwner(), hora2 -> {
            bookViewModel.setSelectedSpot(null);
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
                    chip.setChipBackgroundColorResource(R.color.chip_tipo_plaza_color);
                }
            }
        });
    }

    private void bindChangeTitleAndAddButtonTextWhenEditing(){
        title = binding.title;
        if(bookViewModel.getIsEditing()){
            title.setText(R.string.edit_booking);
            addBookingButton.setText(R.string.edit_booking);
        }else{
            title.setText(R.string.add_booking);
            addBookingButton.setText(R.string.add_booking);
        }
    }

}

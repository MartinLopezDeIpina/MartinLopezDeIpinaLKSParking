package com.lksnext.parking.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BookViewModel extends ViewModel {
    private Parking parking = Parking.getInstance();
    private DataBaseManager db = DataBaseManager.getInstance();

    private final MutableLiveData<Integer> navigateToBookingFragment = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToMainFragment = new MutableLiveData<>();
    private int currentFragment;

    private String lastSelectedHour;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private MutableLiveData<TipoPlaza> selectedTipoPlaza = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> selectedDias = new MutableLiveData<>();
    private MutableLiveData<String> selectedHora1 = new MutableLiveData<>();
    private MutableLiveData<String> selectedHora2 = new MutableLiveData<>();
    private MutableLiveData<List<String>> intermediateSelectedHours = new MutableLiveData<>();
    private MutableLiveData<String> unselectedHora = new MutableLiveData<>();
    private MutableLiveData<List<Long>> availableSpots = new MutableLiveData<>();
    private MutableLiveData<Long> selectedSpot = new MutableLiveData<>();

    private Boolean isEditing;
    private List<Reserva> reservationsToEdit;
    private ReservaCompuesta reservaCompuestaToEdit;
    private boolean editSuccesful;


    private Integer[] dayNumbers = new Integer[7];
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<TipoPlaza> getSelectedTipoPlaza() {
        return selectedTipoPlaza;
    }
    public LiveData<List<Integer>> getSelectedDias() {
        return selectedDias;
    }
    public LiveData<String> getSelectedHora1() {
        return selectedHora1;
    }
    public LiveData<String> getSelectedHora2() {
        return selectedHora2;
    }
    public LiveData<Long> getSelectedSpot() {
        return selectedSpot;
    }
    public LiveData<List<String>> getIntermediateSelectedHours() {
        return intermediateSelectedHours;
    }
    public LiveData<String> getUnselectedHora() {
        return unselectedHora;
    }
    public LiveData<List<Long>> getAvailableSpots() {
        return availableSpots;
    }
    public String getLastSelectedHour() {
        return lastSelectedHour;
    }
    public void setSelectedHora1(String hora) {
        selectedHora1.setValue(hora);
    }
    public void setSelectedHora2(String hora) {
        selectedHora2.setValue(hora);
    }
    public void setSelectedSpot(Long plazaID) {
        selectedSpot.setValue(plazaID);
    }
    private void setLastSelectedHour(String hora) {
        lastSelectedHour = hora;
    }
    public void setIntermediateSelectedHours(List<String> horas) {
        intermediateSelectedHours.setValue(horas);
    }


    public void setCurrentFragment(int currentFragment) {
        this.currentFragment = currentFragment;
    }
    public int getCurrentFragment() {
        return currentFragment;
    }
    public void setNavigateToBookingFragment(Integer navigateToBookingFragment) {
        this.navigateToBookingFragment.setValue(navigateToBookingFragment);
        this.currentFragment = navigateToBookingFragment;
    }
    public LiveData<Integer> getNavigateToBookingFragment() {
        return navigateToBookingFragment;
    }
    public void setNavigateToMainFragment(Boolean navigateToMainFragment) {
        this.navigateToMainFragment.setValue(navigateToMainFragment);
    }
    public LiveData<Boolean> getNavigateToMainFragment() {
        return navigateToMainFragment;
    }



    public boolean getEditSuccesful() {
        return editSuccesful;
    }
    public void setIsEditing(boolean isEditing){
        this.isEditing = isEditing;
    }
    public boolean getIsEditing(){
        return isEditing;
    }
    public void setReservationsToEdit(List<Reserva> reservations, ReservaCompuesta reservaCompuesta) {
        this.editSuccesful = false;
        this.reservaCompuestaToEdit = reservaCompuesta;
        this.reservationsToEdit = reservations;
        boolean isReservaCompuesta = reservaCompuesta != null;

        //Eliminarla para que al editar salgan los valores disponibles
        //En el onPause del fragmento se vuelve a añadir por si se cancela la edición
        reservations.forEach(reserva -> db.deleteBooking(reserva.getId()));

        if(isReservaCompuesta){
            db.deleteReservaCompuesta(reservaCompuesta.getId());
        }
        bindReservationsToEdit();
    }
    private void bindReservationsToEdit(){
        TipoPlaza tipoPlaza = this.reservationsToEdit.get(0).getTipoPlaza();
        List<Integer> dias = this.reservationsToEdit.stream()
                .map(Reserva::getFecha)
                .map(DateUtils::getFechaDay)
                .collect(Collectors.toList());
    }

    public void addEditingReservationIfEditCancelled() {
        if(isEditing && !editSuccesful){
            reservationsToEdit.forEach(reserva -> db.addBookingToDB(reserva));
            List<String> reservasIDs = reservationsToEdit.stream().map(Reserva::getId).collect(Collectors.toList());
            if(reservaCompuestaToEdit != null){
                db.addReservaCompuestaToDB(FirebaseAuth.getInstance().getCurrentUser().getUid(), reservasIDs, reservaCompuestaToEdit.getPlazaID(), reservaCompuestaToEdit.getHora());
            }
        }
    }

    public void setSuccssEditIfEditing() {
        editSuccesful = true;
    }


    public void setPlazasAvailables(Boolean available) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        if(available){
            TipoPlaza tipoPlaza = selectedTipoPlaza.getValue();
            List<String> dias = DateUtils.getFormatedDays(selectedDias.getValue());
            String horaInicio;
            String horaFin;
            String hora1 = selectedHora1.getValue();
            String hora2 = selectedHora2.getValue();
            if(DateUtils.compareStringDates(hora1, hora2) >= 0){
                horaInicio = hora2;
                horaFin = hora1;
            }else{
                horaInicio = hora1;
                horaFin = hora2;
            }

            LiveData<List<Reserva>> bookings = db.getBookingsSpotTypeDayAndHours(tipoPlaza, dias, horaInicio, horaFin);
            bookings.observeForever(new Observer<List<Reserva>>() {
                @Override
                public void onChanged(List<Reserva> conflictingReservas) {

                    List<Long> plazasID = parking.getPlazasIDOfType(tipoPlaza);

                    plazasID.removeAll(conflictingReservas.stream()
                            .map(Reserva::getPlazaID)
                            .collect(Collectors.toList()));

                    availableSpots.setValue(plazasID);
                    result.setValue(true);
                }
            });

        }else{
            availableSpots.setValue(new ArrayList<>());
            result.setValue(false);
        }
    }

    public BookViewModel() {
        dayNumbers = DateUtils.getNextSevenDays();
    }

    public void toggleSelectedHour(String hora){
        if(Objects.equals(selectedHora1.getValue(), hora)){
            setSelectedHour(null, selectedHora1);
            setIntermediateSelectedHours(new ArrayList<>());
            setPlazasAvailables(false);
        }else if(Objects.equals(selectedHora2.getValue(), hora)){
            setSelectedHour(null, selectedHora2);
            setIntermediateSelectedHours(new ArrayList<>());
            setPlazasAvailables(false);
        }else if(selectedHora1.getValue() == null){
            setSelectedHour(hora, selectedHora1);
        }else if(selectedHora2.getValue() == null) {
            setSelectedHour(hora, selectedHora2);
        }else{
            if(selectedHora2.getValue().equals(lastSelectedHour)) {
                setSelectedHour(hora, selectedHora1);
            }else{
                setSelectedHour(hora, selectedHora2);
            }
        }
    }
    private void setSelectedHour(String hour, MutableLiveData<String> selectedHour){
        unselectedHora.setValue(selectedHour.getValue());
        selectedHour.setValue(hour);
        lastSelectedHour = hour;
    }
    public void toggleSelectedTipoPlaza(TipoPlaza tipoPlaza){
        if(selectedTipoPlaza.getValue() == tipoPlaza){
            selectedTipoPlaza.setValue(null);
        }else{
            selectedTipoPlaza.setValue(tipoPlaza);
        }
        emptySelectedHours();
    }

    private void emptySelectedHours(){
        setIntermediateSelectedHours(new ArrayList<>());
        setSelectedHora1(null);
        setSelectedHora2(null);
        setLastSelectedHour(null);
        setPlazasAvailables(false);
    }

    public void emptyBooking(){
       selectedTipoPlaza.setValue(null);
       selectedDias.setValue(new ArrayList<>());
       emptySelectedHours();
    }

    public void toggleDia(Integer dia_index){
        emptySelectedHours();
        int dia = dayNumbers[dia_index];
        List<Integer> dias = selectedDias.getValue();

        if(dias == null){
            dias = new ArrayList<>();
        }
        if(dias.contains(dia)){
            int index = dias.indexOf(dia);
            dias.remove(index);
        }else{
            dias.add(dia);
        }
        selectedDias.setValue(dias);
    }

    public boolean bothHoursSelected(){
        return selectedHora1.getValue() != null && selectedHora2.getValue() != null;
    }

    public LiveData<String> bookSpot(){
        List<String> dias = DateUtils.getFormatedDays(selectedDias.getValue());
        String[] horas = DateUtils.getOrderedHours(selectedHora1.getValue(), selectedHora2.getValue());
        Hora hora = new Hora(horas[0], horas[1]);
        Long plazaID = selectedSpot.getValue();
        String userUuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        boolean reservaCompuesta = dias.size() > 1;
        MediatorLiveData<String> result = new MediatorLiveData<>();
        AtomicInteger counter = new AtomicInteger(0);
        int totalLiveData = dias.size();

        List<String> reservasID = new ArrayList<>();

        for(String dia : dias) {
            Reserva reserva = new Reserva(dia, userUuid, plazaID, hora, reservaCompuesta);
            LiveData<String> reservaID = db.addBookingToDB(reserva);
            if(!reservaCompuesta){
                return reservaID;
            }

            result.addSource(reservaID, s -> {
                if (s != null) {
                    reservasID.add(s);
                }
                if (counter.incrementAndGet() == totalLiveData) {
                    LiveData<String> reservaCompuestaID = db.addReservaCompuestaToDB(userUuid, reservasID, plazaID, hora);
                    result.addSource(reservaCompuestaID, result::setValue);
                }
            });
        }
        return result;
    }

    public LiveData<HashMap<String, Boolean>> getHorasDisponiblesInSelectedSpotTypeAndDays(List<String> fechas_dias, TipoPlaza tipoPlaza){
        MediatorLiveData<HashMap<String, Boolean>> result = new MediatorLiveData<>();
        HashMap<String, Boolean> combinedResult = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(fechas_dias.size());

        for (String dia : fechas_dias) {
            LiveData<HashMap<String, Boolean>> dailyResult = getHorasDisponiblesInSelectedSpotTypeAndDay(dia, tipoPlaza);
            result.addSource(dailyResult, map -> {
                for (Map.Entry<String, Boolean> entry : map.entrySet()) {
                    combinedResult.merge(entry.getKey(), entry.getValue(), Boolean::logicalAnd);
                }
                if (counter.decrementAndGet() == 0) {
                    result.setValue(combinedResult);
                }
            });
        }

        return result;
    }

    private LiveData<HashMap<String, Boolean>> getHorasDisponiblesInSelectedSpotTypeAndDay(String dia, TipoPlaza tipoPlaza){
        int tipoPlazaCount = parking.getPlazas().stream()
                .filter(plaza -> plaza.getTipo().equals(tipoPlaza))
                .collect(Collectors.toList())
                .size();

        MediatorLiveData<HashMap<String, Boolean>> result = new MediatorLiveData<>();
        HashMap<String, Boolean> availableHours = new HashMap<>();

        LiveData<List<Reserva>> reservasDiaTipoPlaza = db.getBookingsSpotDay(dia, tipoPlaza);

        reservasDiaTipoPlaza.observeForever(new Observer<List<Reserva>>() {
            @Override
            public void onChanged(List<Reserva> reservas) {


                for (int i = 0; i < 25; i++) {
                    String hora = String.format("%02d:00", i);
                    int countHoraConflicto = 0;

                    if (reservas == null) {
                        availableHours.put(hora, true);
                        continue;
                    }

                    for (Reserva reserva : reservas) {
                        if (reserva.overlapsHour(hora)) {
                            countHoraConflicto++;
                        }
                    }
                    boolean disponible = countHoraConflicto < tipoPlazaCount;

                    if(DateUtils.horaYaPasada(hora, dia)){
                        disponible = false;
                    }

                    availableHours.put(hora, disponible);
                }

                result.setValue(availableHours);
                reservasDiaTipoPlaza.removeObserver(this);
            }
        });

        return result;
    }


    public static String[] getNextSevenDaysInitials() {
        return DateUtils.getNextSevenDaysInitials();
    }
    public static Integer[] getNextSevenDays() {
        return DateUtils.getNextSevenDays();
    }


}
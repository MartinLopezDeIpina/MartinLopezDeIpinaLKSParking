package com.lksnext.parking.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.TipoPlaza;

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
    private String lastSelectedHour;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private MutableLiveData<TipoPlaza> selectedTipoPlaza = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> selectedDias = new MutableLiveData<>();
    private MutableLiveData<String> selectedHora1 = new MutableLiveData<>();
    private MutableLiveData<String> selectedHora2 = new MutableLiveData<>();
    private MutableLiveData<List<String>> intermediateSelectedHours = new MutableLiveData<>();
    private MutableLiveData<Boolean> horasReservaValidas = new MutableLiveData<>(false);
    private MutableLiveData<String> unselectedHora = new MutableLiveData<>();
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
    public LiveData<List<String>> getIntermediateSelectedHours() {
        return intermediateSelectedHours;
    }
    public LiveData<String> getUnselectedHora() {
        return unselectedHora;
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
    private void setLastSelectedHour(String hora) {
        lastSelectedHour = hora;
    }
    public void setIntermediateSelectedHours(List<String> horas) {
        intermediateSelectedHours.setValue(horas);
    }
    public void setHorasReservaValidas(Boolean available) {
        horasReservaValidas.setValue(available);
    }

    public BookViewModel() {
        dayNumbers = getNextSevenDays();
    }

    public void toggleSelectedHour(String hora){
        if(Objects.equals(selectedHora1.getValue(), hora)){
            setSelectedHour(null, selectedHora1);
            setIntermediateSelectedHours(new ArrayList<>());
            setHorasReservaValidas(false);
        }else if(Objects.equals(selectedHora2.getValue(), hora)){
            setSelectedHour(null, selectedHora2);
            setIntermediateSelectedHours(new ArrayList<>());
            setHorasReservaValidas(false);
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
        setHorasReservaValidas(false);
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


                for (int i = 0; i < 24; i++) {
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
                    availableHours.put(hora, disponible);
                }

                result.setValue(availableHours);
                reservasDiaTipoPlaza.removeObserver(this);
            }
        });

        return result;
    }


    public Integer[] getNextSevenDays() {
        Integer[] nextSevenDays = new Integer[7];
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            nextSevenDays[i] = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return nextSevenDays;
    }

    public String[] getNextSevenDaysInitials() {
        String[] nextSevenDaysInitials = new String[7];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("es", "ES"));

        for (int i = 0; i < 7; i++) {
            String dayOfWeek = sdf.format(calendar.getTime());
            nextSevenDaysInitials[i] = dayOfWeek.substring(0, 1).toUpperCase();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return nextSevenDaysInitials;
    }

}
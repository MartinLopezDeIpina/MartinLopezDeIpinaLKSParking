package com.lksnext.parking.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.data.ReservasCallback;
import com.lksnext.parking.domain.DiaSemana;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.TipoPlaza;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BookViewModel extends ViewModel {
    private Parking parking = Parking.getInstance();
    private DataBaseManager db = DataBaseManager.getInstance();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private MutableLiveData<TipoPlaza> selectedTipoPlaza = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> selectedDias = new MutableLiveData<>();
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

    public BookViewModel() {
        dayNumbers = getNextSevenDays();
    }


    public void toggleSelectedTipoPlaza(TipoPlaza tipoPlaza){
        if(selectedTipoPlaza.getValue() == tipoPlaza){
            selectedTipoPlaza.setValue(null);
        }else{
            selectedTipoPlaza.setValue(tipoPlaza);
        }
    }

    public void toggleDia(Integer dia_index){
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


    private boolean plazaDisponible(long plazaID, Date inicio, Date fin){
        List<Reserva> reservasPlaza = db.getBookingsSpotNotExpired(plazaID);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inicio);

        while (calendar.getTime().before(fin)) {
            Date currentHour = calendar.getTime();
            calendar.add(Calendar.HOUR, 1);
            Date nextHour = calendar.getTime();

            boolean isHourAvailable = reservasPlaza.stream()
                    .noneMatch(reserva -> reserva.overlapsAnyHour(currentHour, nextHour));

            if (isHourAvailable) {
                return true;
            }
        }

        return false;
    }

    private Date getNextSeventhDayEnd(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // Add 7 days to get the next seventh day
        calendar.add(Calendar.DAY_OF_MONTH, 6);

        // Set the time to the end of the day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public List<String> getDatesBetween(Date start, Date end) {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);

        while (calendar.getTime().before(end)) {
            dates.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dates;
    }

    //el primer argumento es el día del mes y el segundo la inicial del mes. De lunes a viernes.
    public String[][] getSemanaActual(){
        String[][] semanaActual = new String[7][2];
        Calendar calendar = Calendar.getInstance();

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        for (int i = 0; i < 7; i++) {
            semanaActual[i][0] = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
            semanaActual[i][1] = new SimpleDateFormat("MMMM", new Locale("es", "ES")).format(calendar.getTime()).substring(0, 1).toUpperCase();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return semanaActual;
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
package com.lksnext.parking.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BookViewModel extends ViewModel {
    private Parking parking = Parking.getInstance();
    private DataBaseManager db = DataBaseManager.getInstance();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private MutableLiveData<TipoPlaza> selectedTipoPlaza = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> selectedDias = new MutableLiveData<>();

    private Integer[] dayNumbers = new Integer[7];


    public BookViewModel() {
        dayNumbers = getNextSevenDays();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<TipoPlaza> getSelectedTipoPlaza() {
        return selectedTipoPlaza;
    }
    public LiveData<List<Integer>> getSelectedDias() {
        return selectedDias;
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


    public LiveData<Boolean> isTipoPlazaDisponibleEstaSemana(TipoPlaza tipo){
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();

        new Thread(() -> {
            Date inicio = new Date();
            Date finDeSemana = getNextSeventhDayEnd();
            List<String> dias = getDatesBetween(inicio, finDeSemana);

            for(String dia : dias){
                LiveData<Boolean> isAvailable = isTipoPlazaDisponibleDia(tipo, dia);
                new Handler(Looper.getMainLooper()).post(() -> {
                    result.addSource(isAvailable, available -> {
                        if (available) {
                            result.postValue(true);
                            result.removeSource(isAvailable);
                        }
                    });
                });
            }
        }).start();

        return result;
    }

    private LiveData<Boolean> isTipoPlazaDisponibleDia(TipoPlaza tipo, String dia){
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();

        List<Plaza> plazas = parking.getPlazas().stream()
                .filter(plaza -> plaza.getTipo().equals(tipo))
                .collect(Collectors.toList());

        for(Plaza plaza : plazas){
            LiveData<Boolean> isAvailable = plazaDisponibleDia(plaza.getId(), dia);
            new Handler(Looper.getMainLooper()).post(() -> {
                result.addSource(isAvailable, available -> {
                    System.out.println("checking" + plaza.getId() + " " + dia);
                    if (available) {
                        result.postValue(true);
                        result.removeSource(isAvailable);
                    }
                });
            });
        }

        return result;
    }
    private LiveData<Boolean> plazaDisponibleDia(long plazaID, String dia){
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        db.getBookingsSpotDay(plazaID, dia, new ReservasCallback() {
            @Override
            public void onCallback(List<Reserva> reservasPlaza) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(dia));
                } catch (ParseException e) {
                    e.printStackTrace();
                    result.postValue(false);
                    return;
                }

                Date inicio = calendar.getTime();
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                Date fin = calendar.getTime();

                while (inicio.before(fin)) {
                    Date currentHour = inicio;
                    calendar.setTime(inicio);
                    calendar.add(Calendar.HOUR, 1);
                    Date nextHour = calendar.getTime();

                    boolean isHourAvailable = reservasPlaza.stream()
                            .noneMatch(reserva -> reserva.overlapsAnyHour(currentHour, nextHour));

                    if (isHourAvailable) {
                        result.postValue(true);
                        return;
                    }

                    inicio = nextHour;
                }

                result.postValue(false);
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
package com.lksnext.parking.viewmodel;

import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.data.ReservasCallback;
import com.lksnext.parking.domain.DiaSemana;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.TipoPlaza;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BookViewModel extends ViewModel {
    private Parking parking = Parking.getInstance();
    private List<Reserva> reservas = new ArrayList<>();
    private DataBaseManager db = DataBaseManager.getInstance();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);


    public BookViewModel() {
        db.getBookings(new ReservasCallback() {
            @Override
            public void onCallback(List<Reserva> reservasDB) {
                reservas = reservasDB;
                isLoading.setValue(false);
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public boolean isTipoPlazaDisponibleEstaSemana(TipoPlaza tipo){
        Date inicio = new Date();
        Date finDeSemana = getSiguineteDomingoFin();

        List<Plaza> plazas = parking.getPlazas();
        return plazas.stream()
                .filter(plaza -> plaza.getTipoPlaza() == tipo)
                .anyMatch(plaza -> plazaDisponible(plaza.getId(), inicio, finDeSemana));
    }

    //una plaza estará disponible esa semana si al menos hay una hora libre en ella
    private boolean plazaDisponible(long plazaID, Date inicio, Date fin){
        List<Reserva> overlapingReservas = reservas.stream()
                .filter(reserva -> reserva.getPlazaID() == plazaID)
                .filter(reserva -> reserva.overlapsAnyHour(inicio, fin))
                .collect(Collectors.toList());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inicio);

        while (calendar.getTime().before(fin)) {
            Date currentHour = calendar.getTime();
            calendar.add(Calendar.HOUR, 1);
            Date nextHour = calendar.getTime();

            boolean isHourAvailable = overlapingReservas.stream()
                    .noneMatch(reserva -> reserva.overlapsAnyHour(currentHour, nextHour));

            if (isHourAvailable) {
                return true;
            }
        }

        return false;
    }

    private Date getSiguineteDomingoFin(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            //si hoy es domingo devolver el final del día
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        } else {
            //si no es domingo devolver el final del próximo domingo
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            calendar.add(Calendar.WEEK_OF_YEAR, 1);

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        }

        return calendar.getTime();
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

    public Integer[] getTwoWeeksDays() {
        Integer[] twoWeeksDays = new Integer[14];
        Calendar calendar = Calendar.getInstance();

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        for (int i = 0; i < 14; i++) {
            twoWeeksDays[i] = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return twoWeeksDays;
    }

}
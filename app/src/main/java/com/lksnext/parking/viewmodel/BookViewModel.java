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


    public BookViewModel() {
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    //chequear si cada tipo de plaza está disponible de forma asíncrona
    public CompletableFuture<Boolean> isTipoPlazaDisponibleEstaSemana(TipoPlaza tipo){
        return CompletableFuture.supplyAsync(() -> {
            Date inicio = new Date();
            Date finDeSemana = getSiguineteDomingoFin();
            List<String> dias = getDatesBetween(inicio, finDeSemana);

            for(String dia : dias){
                if(isTipoPlazaDisponibleDia(tipo, dia)){
                    return true;
                }
            }
            return false;
        });
    }

    //Dentro de cada tipo de plaza, chequear si hay alguna plaza disponible ese día de forma síncrona
    private boolean isTipoPlazaDisponibleDia(TipoPlaza tipo, String dia){
        List<Plaza> plazas = parking.getPlazas().stream()
                .filter(plaza -> plaza.getTipo().equals(tipo))
                .collect(Collectors.toList());


        for(Plaza plaza : plazas){
            System.out.println("checking" + plaza.getId() + " " + dia);
            if(plazaDisponibleDia(plaza.getId(), dia)){
                return true;
            }
        }
        return false;
    }

    //Dentro de cada plaza chquear si hay alguna plaza disponible ese día de forma síncrona
    private boolean plazaDisponibleDia(long plazaID, String dia){
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        db.getBookingsSpotDay(plazaID, dia, new ReservasCallback() {
            @Override
            public void onCallback(List<Reserva> reservasPlaza) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(dia));
                } catch (ParseException e) {
                    e.printStackTrace();
                    future.complete(false);
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
                        future.complete(true);
                        return;
                    }

                    inicio = nextHour;
                }

                future.complete(false);
            }
        });

        return future.join();
    }

    //una plaza estará disponible esa semana si al menos hay una hora libre en ella
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

    private Date getSiguineteDomingoFin(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // Set the day to Sunday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        // Add one week to always get the next Sunday
        calendar.add(Calendar.WEEK_OF_YEAR, 1);

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
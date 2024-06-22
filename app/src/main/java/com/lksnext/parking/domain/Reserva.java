package com.lksnext.parking.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class Reserva {

    String fecha, usuarioID, id;

    Long plazaID;

    //Para facilitar las consultas a la base de datos
    TipoPlaza tipoPlaza;

    Hora hora;

    boolean insideReservaMultiple;

    public Reserva() {

    }

    public Reserva(String fecha, String usuarioID, Integer plazaID, Hora hora, Boolean insideReservaMultiple){
        this(fecha, usuarioID, plazaID.longValue(), hora, insideReservaMultiple);
    }

    public Reserva(String fecha, String usuarioID, Long plazaID, Hora hora, Boolean insideReservaMultiple) {
        this.id = UUID.randomUUID().toString();
        this.fecha = fecha;
        this.usuarioID = usuarioID;
        this.plazaID = plazaID;
        this.hora = hora;
        this.insideReservaMultiple = insideReservaMultiple;
        this.tipoPlaza = Parking.getInstance().getTipoPlazaReserva(plazaID);
    }
    public Reserva(String fecha, Hora hora){
        this.fecha = fecha;
        this.hora = hora;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public Long getPlazaID() {
        return plazaID;
    }

    public void setPlazaID(Long plazaID) {
        this.plazaID = plazaID;
    }
    public TipoPlaza getTipoPlaza() {
        return tipoPlaza;
    }
    public void setTipoPlaza(TipoPlaza tipoPlaza) {
        this.tipoPlaza = tipoPlaza;
    }
    public String getId() {
        return id;
    }
    public boolean isInsideReservaMultiple() {
        return insideReservaMultiple;
    }

    public DiaSemana getDiaSemana() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(fecha);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 1) {
                return DiaSemana.DOMINGO;
            } else {
                return DiaSemana.values()[dayOfWeek - 2];
            }
        } catch (ParseException e) {
            return null;
        }
    }

    @PropertyName("hora")
    public Hora getHora() {
        return hora;
    }
    @PropertyName("hora")
    public void setHora(Hora hora) {
        this.hora = hora;
    }


    @Exclude
    public boolean isCaducada() {
        Date book_date = getParsedDate();

        if (book_date == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTimeStr = fecha + " " + hora.getHoraFin();
        Date horaFinDate;
        try {
            horaFinDate = sdf.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(book_date);
        cal2.setTime(new Date());

        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        if(sameDay) {
            return horaFinDate != null && horaFinDate.getTime() < System.currentTimeMillis();
        } else {
            return book_date.compareTo(new Date()) <= 0;
        }
    }


    private Date getParsedDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(fecha);
        } catch (ParseException e) {
            return null;
        }
    }

    private Date getTransformedDate(boolean isInicio){
        String hourStr;
        if(isInicio){
            hourStr = hora.getHoraInicio();
        }else{
            hourStr = hora.getHoraFin();
        }


        String dateTimeStr = fecha + " " + hourStr;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    public boolean overlapsAnyHour(Date inicio, Date fin){
        Date dateInicio = getTransformedDate(true);
        Date dateFin = getTransformedDate(false);

        boolean overlapsFromInicio = dateInicio.compareTo(inicio) < 0 && dateFin.compareTo(inicio) > 0;
        boolean overlapsFromFin = dateInicio.compareTo(fin) < 0 && dateFin.compareTo(fin) > 0;
        boolean overlapsInside = dateInicio.compareTo(inicio) > 0 && dateFin.compareTo(fin) < 0;
        boolean overlapsOutside = dateInicio.compareTo(inicio) < 0 && dateFin.compareTo(fin) > 0;

        return overlapsFromInicio || overlapsFromFin || overlapsInside || overlapsOutside;
    }

    public boolean overlapsHour(String hora) {
        return this.hora.getHoraInicio().compareTo(hora) < 0 && this.hora.getHoraFin().compareTo(hora) >= 0;
    }

    public boolean getEsCompuesta() {
        return insideReservaMultiple;
    }
}

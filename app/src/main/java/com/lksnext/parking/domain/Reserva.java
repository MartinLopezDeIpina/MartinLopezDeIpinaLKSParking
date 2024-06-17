package com.lksnext.parking.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.google.firebase.firestore.PropertyName;
import java.util.UUID;

public class Reserva {

    String fecha, usuarioID, id;

    Integer plazaID;

    Hora hora;

    boolean insideReservaMultiple;

    public Reserva() {

    }

    public Reserva(String fecha, String usuarioID, Integer plazaID, Hora hora, Boolean insideReservaMultiple) {
        this.id = UUID.randomUUID().toString();
        this.fecha = fecha;
        this.usuarioID = usuarioID;
        this.plazaID = plazaID;
        this.hora = hora;
        this.insideReservaMultiple = insideReservaMultiple;
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

    public Integer getPlazaID() {
        return plazaID;
    }

    public void setPlazaID(Integer plazaID) {
        this.plazaID = plazaID;
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
            return DiaSemana.values()[dayOfWeek - 1];
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


    public boolean isCaducada() {
        Date today = getParsedDate();

        if (today == null) {
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

        return today.compareTo(new Date()) >= 0 && (horaFinDate != null && horaFinDate.getTime() > System.currentTimeMillis());
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
}

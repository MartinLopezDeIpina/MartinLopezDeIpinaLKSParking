package com.lksnext.parking.domain;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ParkingTest {
    Parking parking;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        parking = Parking.getInstance();

    }

    @Test
    public void testGetPlazasIDOfType() throws InterruptedException{
        Plaza plaza1 = new Plaza(1L, TipoPlaza.COCHE);
        Plaza plaza2 = new Plaza(2L, TipoPlaza.MOTO);
        Plaza plaza3 = new Plaza(3L, TipoPlaza.COCHE);

        parking.setPlazas(Arrays.asList(plaza1, plaza2, plaza3));

        Thread.sleep(100);
        List<Long> cocheIDs = parking.getPlazasIDOfType(TipoPlaza.COCHE);
        assertTrue(cocheIDs.contains(plaza1.getId()));
        assertTrue(cocheIDs.contains(plaza3.getId()));
        assertFalse(cocheIDs.contains(plaza2.getId()));

        List<Long> motoIDs = parking.getPlazasIDOfType(TipoPlaza.MOTO);
        assertTrue(motoIDs.contains(plaza2.getId()));
        assertFalse(motoIDs.contains(plaza1.getId()));
        assertFalse(motoIDs.contains(plaza3.getId()));
    }

    @Test
    public void testGetReservaCompuestaThatContains() throws InterruptedException{
        String expectedID = "3";

        Reserva reserva = new Reserva("2021-06-01", "3", 1L, new Hora("10:30", "11:30"), true, TipoPlaza.COCHE);
        List<Reserva> reservas = Arrays.asList(reserva);
        List<String> reservasID = Arrays.asList(reserva.getId());

        ReservaCompuesta reservaCompuesta = new ReservaCompuesta("3", "3", reservasID, 1L, new Hora("10:30", "11:30"));

        parking.setReservasCompuestas(Arrays.asList(reservaCompuesta));

        Thread.sleep(100);

        ReservaCompuesta obtainedReservaCompuesta = parking.getReservaCompuestaThatContains(reserva.getId());
        String obtainedID = obtainedReservaCompuesta.getId();

        assertEquals(expectedID, obtainedID);
    }


    @Test
    public void testIsReservaOfType() throws InterruptedException {
        Plaza plaza = new Plaza(1L, TipoPlaza.COCHE);
        parking.setPlazas(Arrays.asList(plaza));

        Reserva reserva = new Reserva("1", "1", plaza.getId(), new Hora("10:30", "11:30"), true, TipoPlaza.COCHE);
        parking.setReservas(Arrays.asList(reserva));

        Thread.sleep(100);

        TipoPlaza expectedTipoPlaza = TipoPlaza.COCHE;
        TipoPlaza obtainedTipoPlaza = parking.getTipoPlazaReserva(reserva.getPlazaID());

        assertEquals(expectedTipoPlaza, obtainedTipoPlaza);
    }
    @Test
    public void testGetNumPlazasCoche() throws InterruptedException {
        Plaza plaza1 = new Plaza(1L, TipoPlaza.COCHE);
        Plaza plaza2 = new Plaza(2L, TipoPlaza.MOTO);
        Plaza plaza3 = new Plaza(3L, TipoPlaza.COCHE);

        parking.setPlazas(Arrays.asList(plaza1, plaza2, plaza3));

        Thread.sleep(100);
        int numPlazasCoche = parking.getNumPlazasCoche();
        assertEquals(2, numPlazasCoche);
    }

    @Test
    public void testGetNumPlazasMoto() throws InterruptedException {
        Plaza plaza1 = new Plaza(1L, TipoPlaza.COCHE);
        Plaza plaza2 = new Plaza(2L, TipoPlaza.MOTO);
        Plaza plaza3 = new Plaza(3L, TipoPlaza.MOTO);

        parking.setPlazas(Arrays.asList(plaza1, plaza2, plaza3));

        Thread.sleep(100);
        int numPlazasMoto = parking.getNumPlazasMoto();
        assertEquals(2, numPlazasMoto);
    }

    @Test
    public void testGetNumPlazasElectrico() throws InterruptedException {
        Plaza plaza1 = new Plaza(1L, TipoPlaza.ELECTRICO);
        Plaza plaza2 = new Plaza(2L, TipoPlaza.MOTO);
        Plaza plaza3 = new Plaza(3L, TipoPlaza.ELECTRICO);

        parking.setPlazas(Arrays.asList(plaza1, plaza2, plaza3));

        Thread.sleep(100);
        int numPlazasElectrico = parking.getNumPlazasElectrico();
        assertEquals(2, numPlazasElectrico);
    }

    @Test
    public void testGetNumPlazasEspecial() throws InterruptedException {
        Plaza plaza1 = new Plaza(1L, TipoPlaza.DISCAPACITADO);
        Plaza plaza2 = new Plaza(2L, TipoPlaza.COCHE);
        Plaza plaza3 = new Plaza(3L, TipoPlaza.DISCAPACITADO);

        parking.setPlazas(Arrays.asList(plaza1, plaza2, plaza3));

        Thread.sleep(100);
        int numPlazasEspecial = parking.getNumPlazasEspecial();
        assertEquals(2, numPlazasEspecial);
    }
    @Test
    public void testIsReservaCompuesta() throws InterruptedException {
        Reserva reserva1 = new Reserva("2021-06-01", "1", 1L, new Hora("10:30", "11:30"), true, TipoPlaza.COCHE);
        Reserva reserva2 = new Reserva("2021-06-01", "2", 2L, new Hora("11:30", "12:30"), true, TipoPlaza.MOTO);
        List<String> reservasID1 = Arrays.asList(reserva1.getId());
        List<String> reservasID2 = Arrays.asList(reserva2.getId());

        ReservaCompuesta reservaCompuesta1 = new ReservaCompuesta("1", "1", reservasID1, 1L, new Hora("10:30", "11:30"));
        ReservaCompuesta reservaCompuesta2 = new ReservaCompuesta("2", "2", reservasID2, 2L, new Hora("11:30", "12:30"));

        parking.setReservasCompuestas(Arrays.asList(reservaCompuesta1, reservaCompuesta2));

        Thread.sleep(100);

        assertTrue(parking.isReservaCompuesta("1"));
        assertTrue(parking.isReservaCompuesta("2"));
        assertFalse(parking.isReservaCompuesta("3"));
    }
}
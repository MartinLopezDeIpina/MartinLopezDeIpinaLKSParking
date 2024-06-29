package com.lksnext.parking.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.util.LiveDataTestUtil;
import com.lksnext.parking.viewmodel.BookViewModel;
import com.lksnext.parking.viewmodel.MainViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class BookViewModelTest {

    private DataBaseManager db;

    private MockedStatic<DataBaseManager> mockStaticDb;
    private BookViewModel bookViewModel;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
       db = mock(DataBaseManager.class);
       mockStaticDb = Mockito.mockStatic(DataBaseManager.class);
       mockStaticDb.when(DataBaseManager::getInstance).thenReturn(db);

       bookViewModel = new BookViewModel(db);
    }

    @After
    public void tearDown() throws Exception {
        mockStaticDb.close();
    }

    @Test
    public void emptyBooking() {
        System.out.println("ejecutando test emptyBooking()");
        assertTrue(true);
    }

    @Test
    public void getSelectedHora1() throws InterruptedException{

        System.out.println("ejecutando test getSelectedHora1()");
        System.out.println("ejecutando test getSelectedHora1()");

        String expectedHora = "12:00";
        String countValue;

        String selectedHora = LiveDataTestUtil.getValue(bookViewModel.getSelectedHora1());
        assertNull(selectedHora);

        bookViewModel.setSelectedHora1(expectedHora);
        countValue = LiveDataTestUtil.getValue(bookViewModel.getSelectedHora1());

        assertEquals(expectedHora, countValue);
        assertTrue(1 > 0);
    }
    @Test
    public void deleteEditedBookingfromDB_callsDeleteReservaCompuesta() {
        List<Reserva> reservas = new ArrayList<>();

        long id = 1;
        reservas.add(new Reserva("2021-06-01", "a", id , new Hora("10:30", "11:30"), false, TipoPlaza.COCHE));

        bookViewModel.setReservationsToEdit(reservas,
                new ReservaCompuesta("a", new ArrayList<>(), id, new Hora("10:00", "11:00")));

        bookViewModel.deleteEditedBookingfromDB();

        Mockito.verify(db, Mockito.atLeastOnce()).deleteBooking(anyObject());
        Mockito.verify(db, Mockito.atLeastOnce()).deleteReservaCompuesta(anyObject());
    }

    @Test
    public void deleteEditedBookingfromDB_callsDeleteBooking() {
        List<Reserva> reservas = new ArrayList<>();

        long id = 1;
        reservas.add(new Reserva("2021-06-01", "a", id , new Hora("10:30", "11:30"), false, TipoPlaza.COCHE));

        bookViewModel.setReservationsToEdit(reservas, null);

        bookViewModel.deleteEditedBookingfromDB();

        Mockito.verify(db, Mockito.atLeastOnce()).deleteBooking(anyObject());
        Mockito.verify(db, Mockito.never()).deleteReservaCompuesta(anyObject());
    }

}
package com.lksnext.parking.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.util.LiveDataTestUtil;
import com.lksnext.parking.viewmodel.BookViewModel;
import com.lksnext.parking.viewmodel.MainViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
        assertTrue(true);
    }

    @Test
    public void getSelectedHora1() throws InterruptedException{

        String expectedHora = "12:00";
        String countValue;

        String selectedHora = LiveDataTestUtil.getValue(bookViewModel.getSelectedHora1());
        assertNull(selectedHora);

        bookViewModel.setSelectedHora1(expectedHora);
        countValue = LiveDataTestUtil.getValue(bookViewModel.getSelectedHora1());

        assertEquals(expectedHora, countValue);
    }

}
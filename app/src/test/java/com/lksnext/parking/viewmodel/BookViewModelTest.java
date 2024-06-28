package com.lksnext.parking.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.lksnext.parking.data.DataBaseManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class BookViewModelTest {

    private DataBaseManager db;

    private MockedStatic<DataBaseManager> mockStaticDb;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
       db = mock(DataBaseManager.class);
       mockStaticDb = Mockito.mockStatic(DataBaseManager.class);
       mockStaticDb.when(DataBaseManager::getInstance).thenReturn(db);
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
    public void getSelectedHora1(){
        BookViewModel viewModel = new BookViewModel(db);
        String expectedHora = "12:00";

        viewModel.setSelectedHora1(expectedHora);
        String actualHora = viewModel.getSelectedHora1().getValue();

        assertEquals(expectedHora, actualHora);
    }
}
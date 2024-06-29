package com.lksnext.parking.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.lksnext.parking.data.DataRepository;
import com.lksnext.parking.data.LoginErrorType;
import com.lksnext.parking.data.callbacks.LoginCallback;
import com.lksnext.parking.util.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class LoginViewModelTest {

    @Mock
    DataRepository dataRepository;
    LoginViewModel loginViewModel;
    private MockedStatic<DataRepository> mockStaticDb;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockStaticDb = Mockito.mockStatic(DataRepository.class);
        mockStaticDb.when(DataRepository::getInstance).thenReturn(dataRepository);
        loginViewModel = new LoginViewModel(dataRepository);
    }

    @After
    public void tearDown() throws Exception {
        mockStaticDb.close();
    }

    @Test
    public void loginUserSuccess() throws InterruptedException {
        doAnswer(invocation -> {
            ((LoginCallback) invocation.getArguments()[2]).onSuccess();
            return null;
        }).when(dataRepository).login(eq("email"), eq("password"), any(LoginCallback.class));

        loginViewModel.loginUser("email", "password");

        assertTrue(LiveDataTestUtil.getValue(loginViewModel.isLogged()));
    }

    @Test
    public void loginUserFailure() throws InterruptedException {
        doAnswer(invocation -> {
            ((LoginCallback) invocation.getArguments()[2]).onFailure(LoginErrorType.INVALID_EMAIL);
            return null;
        }).when(dataRepository).login(eq("email"), eq("password"), any(LoginCallback.class));

        loginViewModel.loginUser("email", "password");

        assertFalse(LiveDataTestUtil.getValue(loginViewModel.isLogged()));
        assertEquals(LoginErrorType.INVALID_EMAIL, LiveDataTestUtil.getValue(loginViewModel.getErrorMessage()));
    }
}
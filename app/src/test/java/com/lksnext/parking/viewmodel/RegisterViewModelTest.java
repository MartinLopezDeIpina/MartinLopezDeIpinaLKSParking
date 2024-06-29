package com.lksnext.parking.viewmodel;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.lksnext.parking.data.DataRepository;
import com.lksnext.parking.data.RegisterErrorType;
import com.lksnext.parking.data.callbacks.EmailVerificationCallback;
import com.lksnext.parking.data.callbacks.RegisterCallback;
import com.lksnext.parking.util.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class RegisterViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private DataRepository dataRepository;

    private RegisterViewModel registerViewModel;

    @Before
    public void setUp() {
        dataRepository = Mockito.mock(DataRepository.class);
        registerViewModel = new RegisterViewModel(dataRepository);
    }
    @Test
    public void registerUser_successfulRegistration_setsPendingVerificationEmail() throws InterruptedException{
        String email = "test@example.com";
        String password = "password";
        String name = "John Doe";
        String phone = "1234567890";

        ArgumentCaptor<RegisterCallback> registerCallbackCaptor = ArgumentCaptor.forClass(RegisterCallback.class);

        registerViewModel.registerUser(email, password, name, phone);
        Mockito.verify(dataRepository).register(Mockito.eq(email), Mockito.eq(password), Mockito.eq(name), Mockito.eq(phone), registerCallbackCaptor.capture());

        registerCallbackCaptor.getValue().onSuccess();

        assertEquals(email, LiveDataTestUtil.getValue(registerViewModel.getPendingVerificationEmail()));
    }

    @Test
    public void registerUser_failedRegistration_setsRegisterError() throws InterruptedException{
        String email = "test@example.com";
        String password = "password";
        String name = "John Doe";
        String phone = "1234567890";
        RegisterErrorType errorType = RegisterErrorType.UNKNOWN_ERROR;

        ArgumentCaptor<RegisterCallback> registerCallbackCaptor = ArgumentCaptor.forClass(RegisterCallback.class);

        registerViewModel.registerUser(email, password, name, phone);
        Mockito.verify(dataRepository).register(Mockito.eq(email), Mockito.eq(password), Mockito.eq(name), Mockito.eq(phone), registerCallbackCaptor.capture());

        registerCallbackCaptor.getValue().onRegisterFailure(errorType);

        assertEquals(errorType, LiveDataTestUtil.getValue(registerViewModel.getErrorMessage()));
    }

    @Test
    public void bindEmailVerification_successfulVerification_setsRegisteredEmail() throws InterruptedException{
        String email = "test@example.com";

        ArgumentCaptor<EmailVerificationCallback> emailVerificationCallbackCaptor = ArgumentCaptor.forClass(EmailVerificationCallback.class);

        registerViewModel.registerUser(email, "password", "John Doe", "1234567890");

        ArgumentCaptor<RegisterCallback> registerCallbackCaptor = ArgumentCaptor.forClass(RegisterCallback.class);
        Mockito.verify(dataRepository).register(Mockito.eq(email), Mockito.eq("password"), Mockito.eq("John Doe"), Mockito.eq("1234567890"), registerCallbackCaptor.capture());
        registerCallbackCaptor.getValue().onSuccess();

        Mockito.verify(dataRepository).sendVerificationEmail(emailVerificationCallbackCaptor.capture());

        emailVerificationCallbackCaptor.getValue().onSuccess();

        assertEquals(email, LiveDataTestUtil.getValue(registerViewModel.getRegisteredEmail()));
    }

    @Test
    public void bindEmailVerification_failedVerification_setsRegisterError() throws InterruptedException{
        String email = "test@example.com";

        ArgumentCaptor<EmailVerificationCallback> emailVerificationCallbackCaptor = ArgumentCaptor.forClass(EmailVerificationCallback.class);

        registerViewModel.registerUser(email, "password", "John Doe", "1234567890");

        ArgumentCaptor<RegisterCallback> registerCallbackCaptor = ArgumentCaptor.forClass(RegisterCallback.class);
        Mockito.verify(dataRepository).register(Mockito.eq(email), Mockito.eq("password"), Mockito.eq("John Doe"), Mockito.eq("1234567890"), registerCallbackCaptor.capture());
        registerCallbackCaptor.getValue().onSuccess();

        Mockito.verify(dataRepository).sendVerificationEmail(emailVerificationCallbackCaptor.capture());

        emailVerificationCallbackCaptor.getValue().onFailure(RegisterErrorType.UNKNOWN_ERROR);

        assertEquals(RegisterErrorType.UNKNOWN_ERROR, LiveDataTestUtil.getValue(registerViewModel.getErrorMessage()));
    }

    @Test
    public void getPendingVerificationEmail() {
    }

    @Test
    public void registerUser() {
    }
}
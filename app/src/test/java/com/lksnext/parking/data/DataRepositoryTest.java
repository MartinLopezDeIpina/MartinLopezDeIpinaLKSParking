package com.lksnext.parking.data;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parking.data.callbacks.EmailVerificationCallback;
import com.lksnext.parking.data.callbacks.LoginCallback;
import com.lksnext.parking.data.callbacks.RegisterCallback;
import com.lksnext.parking.util.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

public class DataRepositoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    @Mock
    private FirebaseAuth firebaseAuth;
    @Mock
    private FirebaseUser firebaseUser;
    @Mock
    private Task<AuthResult> authResultTask;
    @Mock
    private DataBaseManager dataBaseManager;
    @Mock
    private FirebaseUser pendingFirebaseUser;
    @Mock
    private Task<Void> verificationTask;
    private DataRepository dataRepository;

    @Before
    public void setUp() {
        firebaseAuth = Mockito.mock(FirebaseAuth.class);
        firebaseUser = Mockito.mock(FirebaseUser.class);
        authResultTask = Mockito.mock(Task.class);
        dataBaseManager = Mockito.mock(DataBaseManager.class);
        dataRepository = Mockito.spy(new DataRepository(firebaseAuth, dataBaseManager));
        pendingFirebaseUser = Mockito.mock(FirebaseUser.class);
        verificationTask = Mockito.mock(Task.class);

        dataRepository.setPendingFirebaseUser(pendingFirebaseUser);
    }

    @Test
    public void login_withEmptyEmail_callsOnFailureWithEmptyEmailError() {
        LoginCallback callback = Mockito.mock(LoginCallback.class);
        dataRepository.login("", "password", callback);
        Mockito.verify(callback).onFailure(LoginErrorType.EMPTY_EMAIL);
    }

    @Test
    public void login_withEmptyPassword_callsOnFailureWithEmptyPasswordError() {
        LoginCallback callback = Mockito.mock(LoginCallback.class);
        dataRepository.login("email@gmail.com", "", callback);
        Mockito.verify(callback).onFailure(LoginErrorType.EMPTY_PASSWORD);
    }

    @Test
    public void login_withInvalidEmail_callsOnFailureWithInvalidEmail() {
        LoginCallback callback = Mockito.mock(LoginCallback.class);
        Mockito.doReturn(false).when(dataRepository).emailValidatesRegex(anyString());
        dataRepository.login("emailsinlaestructuracorrecta", "qqqqqq", callback);
        Mockito.verify(callback).onFailure(LoginErrorType.INVALID_EMAIL);
    }

    @Test
    public void login_withInvalidPassword_callsOnFailureWithInvalidPassword() {
        LoginCallback callback = Mockito.mock(LoginCallback.class);
        Mockito.doReturn(true).when(dataRepository).emailValidatesRegex(anyString());
        dataRepository.login("email@gmail.com", "qqq", callback);
        Mockito.verify(callback).onFailure(LoginErrorType.INVALID_PASSWORD);
    }

    @Test
    public void login_withValidCredentialsAndSuccessfulTask_callsOnSuccess() {
        LoginCallback callback = Mockito.mock(LoginCallback.class);

        Mockito.doReturn(true).when(dataRepository).emailValidatesRegex(anyString());

        Mockito.when(authResultTask.isSuccessful()).thenReturn(true);
        Mockito.when(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(authResultTask);

        Mockito.when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        Mockito.when(firebaseUser.isEmailVerified()).thenReturn(true);

        dataRepository.login("email@gmail.com", "validPassword", callback);

        ArgumentCaptor<OnCompleteListener<AuthResult>> argumentCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        Mockito.verify(authResultTask).addOnCompleteListener(argumentCaptor.capture());

        argumentCaptor.getValue().onComplete(authResultTask);

        Mockito.verify(callback).onSuccess();
    }

    @Test
    public void login_withValidCredentialsAndUnsuccessfulTask_callsOnFailure() {
        LoginCallback callback = Mockito.mock(LoginCallback.class);

        Mockito.doReturn(true).when(dataRepository).emailValidatesRegex(anyString());

        Mockito.when(authResultTask.isSuccessful()).thenReturn(false);
        Mockito.when(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(authResultTask);

        Exception authException = new Exception("Authentication failed");
        Mockito.when(authResultTask.getException()).thenReturn(authException);

        Mockito.doReturn(LoginErrorType.UNKNOWN_ERROR).when(dataRepository).getLoginErrorMessage(authException);

        dataRepository.login("email@gmail.com", "validPassword", callback);

        ArgumentCaptor<OnCompleteListener<AuthResult>> argumentCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        Mockito.verify(authResultTask).addOnCompleteListener(argumentCaptor.capture());

        argumentCaptor.getValue().onComplete(authResultTask);

        Mockito.verify(callback).onFailure(LoginErrorType.UNKNOWN_ERROR);
    }

    @Test
    public void sendVerificationEmail_withSuccessfulTask_callsOnSuccess() {
        EmailVerificationCallback callback = Mockito.mock(EmailVerificationCallback.class);

        Mockito.when(verificationTask.isSuccessful()).thenReturn(true);
        Mockito.when(pendingFirebaseUser.sendEmailVerification()).thenReturn(verificationTask);

        dataRepository.sendVerificationEmail(callback);

        ArgumentCaptor<OnCompleteListener<Void>> argumentCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        Mockito.verify(verificationTask).addOnCompleteListener(argumentCaptor.capture());

        argumentCaptor.getValue().onComplete(verificationTask);

        Mockito.verify(callback).onSuccess();
    }

    @Test
    public void sendVerificationEmail_withUnsuccessfulTask_callsOnFailure() {
        EmailVerificationCallback callback = Mockito.mock(EmailVerificationCallback.class);

        Mockito.when(verificationTask.isSuccessful()).thenReturn(false);
        Mockito.when(pendingFirebaseUser.sendEmailVerification()).thenReturn(verificationTask);

        dataRepository.sendVerificationEmail(callback);

        ArgumentCaptor<OnCompleteListener<Void>> argumentCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        Mockito.verify(verificationTask).addOnCompleteListener(argumentCaptor.capture());

        argumentCaptor.getValue().onComplete(verificationTask);

        Mockito.verify(callback).onFailure(RegisterErrorType.UNKNOWN_ERROR);
    }

    @Test
    public void emailValid_withEmptyEmail_callsOnFailureWithEmptyEmailError() {
        LoginCallback loginCallback = Mockito.mock(LoginCallback.class);

        dataRepository.emailValid("", loginCallback);

        Mockito.verify(loginCallback).onFailure(LoginErrorType.EMPTY_EMAIL);
    }

    @Test
    public void emailValid_withInvalidEmail_callsOnFailureWithInvalidEmailError() {
        LoginCallback loginCallback = Mockito.mock(LoginCallback.class);

        Mockito.doReturn(false).when(dataRepository).emailValidatesRegex(anyString());

        dataRepository.emailValid("invalid-email", loginCallback);

        Mockito.verify(loginCallback).onFailure(LoginErrorType.INVALID_EMAIL);
    }

    @Test
    public void emailValid_withExistingEmail_callsOnSuccess() throws InterruptedException {
        LoginCallback loginCallback = Mockito.mock(LoginCallback.class);
        String email = "email@gmail.com";

        Mockito.doReturn(true).when(dataRepository).emailValidatesRegex(anyString());

        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Mockito.when(dataBaseManager.getUserExists(email)).thenReturn(liveData);

        dataRepository.emailValid(email, loginCallback);

        liveData.postValue(true);

        Boolean exists = LiveDataTestUtil.getValue(liveData);

        if (exists) {
            Mockito.verify(loginCallback).onSuccess();
        } else {
            Mockito.verify(loginCallback).onFailure(LoginErrorType.USER_NOT_FOUND);
        }
    }

    @Test
    public void emailValid_withNonExistingEmail_callsOnFailureWithUserNotFoundError() throws InterruptedException {
        LoginCallback loginCallback = Mockito.mock(LoginCallback.class);
        String email = "email@gmail.com";

        Mockito.doReturn(true).when(dataRepository).emailValidatesRegex(anyString());

        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Mockito.when(dataBaseManager.getUserExists(email)).thenReturn(liveData);

        dataRepository.emailValid(email, loginCallback);

        liveData.postValue(false);

        Boolean exists = LiveDataTestUtil.getValue(liveData);

        if (exists) {
            Mockito.verify(loginCallback).onSuccess();
        } else {
            Mockito.verify(loginCallback).onFailure(LoginErrorType.USER_NOT_FOUND);
        }
    }
    @Test
    public void register_withValidCredentials_callsOnSuccess() {
        RegisterCallback callback = Mockito.mock(RegisterCallback.class);
        String email = "email@gmail.com";
        String password = "password";
        String name = "John Doe";
        String phone = "1234567890";

        Mockito.when(authResultTask.isSuccessful()).thenReturn(true);
        Mockito.when(firebaseAuth.createUserWithEmailAndPassword(email, password)).thenReturn(authResultTask);
        Mockito.when(firebaseAuth.getCurrentUser()).thenReturn(pendingFirebaseUser);

        dataRepository.register(email, password, name, phone, callback);

        ArgumentCaptor<OnCompleteListener<AuthResult>> argumentCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        Mockito.verify(authResultTask).addOnCompleteListener(argumentCaptor.capture());

        argumentCaptor.getValue().onComplete(authResultTask);

        Mockito.verify(callback).onSuccess();
    }

    @Test
    public void register_withInvalidCredentials_callsOnRegisterFailure() {
        RegisterCallback callback = Mockito.mock(RegisterCallback.class);
        String email = "email@gmail.com";
        String password = "password";
        String name = "John Doe";
        String phone = "1234567890";

        Mockito.when(authResultTask.isSuccessful()).thenReturn(false);
        Mockito.when(firebaseAuth.createUserWithEmailAndPassword(email, password)).thenReturn(authResultTask);

        dataRepository.register(email, password, name, phone, callback);

        ArgumentCaptor<OnCompleteListener<AuthResult>> argumentCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        Mockito.verify(authResultTask).addOnCompleteListener(argumentCaptor.capture());

        argumentCaptor.getValue().onComplete(authResultTask);

        Mockito.verify(callback).onRegisterFailure(anyObject());
    }





}
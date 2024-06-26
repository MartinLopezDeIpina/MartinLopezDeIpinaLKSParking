package com.lksnext.parking.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parking.data.DataRepository;
import com.lksnext.parking.data.RegisterErrorType;
import com.lksnext.parking.data.callbacks.EmailVerificationCallback;
import com.lksnext.parking.data.callbacks.RegisterCallback;
import com.lksnext.parking.util.annotations.Getter;

public class RegisterViewModel extends ViewModel {
    MutableLiveData<RegisterErrorType> registerError = new MutableLiveData<>(null);
    MutableLiveData<String> registeredEmail = new MutableLiveData<>(null);
    MutableLiveData<String> pendingVerificationEmail = new MutableLiveData<>(null);

    private DataRepository dataRepository;

    public RegisterViewModel() {
        this(DataRepository.getInstance());
    }
    public RegisterViewModel(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Getter
    public LiveData<RegisterErrorType> getErrorMessage() {
        return registerError;
    }
    @Getter
    public LiveData<String> getRegisteredEmail() {
        return registeredEmail;
    }
    @Getter
    public LiveData<String> getPendingVerificationEmail() {
        return pendingVerificationEmail;
    }

    public void registerUser(String email, String password, String name, String phone) {
        dataRepository.register(email, password, name, phone, new RegisterCallback() {
            @Override
            public void onSuccess() {
                pendingVerificationEmail.setValue(email);
                bindEmailVerification(email);
            }

            @Override
            public void onRegisterFailure(RegisterErrorType error) {
                registerError.setValue(error);
            }
        });
    }

    private void bindEmailVerification(String email){

        dataRepository.sendVerificationEmail(new EmailVerificationCallback() {
            @Override
            public void onSuccess() {
                registeredEmail.setValue(email);
            }

            @Override
            public void onFailure(RegisterErrorType error) {
                registerError.setValue(RegisterErrorType.UNKNOWN_ERROR);
            }
        });
    }
}

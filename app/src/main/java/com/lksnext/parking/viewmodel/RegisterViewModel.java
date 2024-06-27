package com.lksnext.parking.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parking.data.DataRepository;
import com.lksnext.parking.data.RegisterErrorType;
import com.lksnext.parking.domain.callbacks.EmailVerificationCallback;
import com.lksnext.parking.domain.callbacks.RegisterCallback;

public class RegisterViewModel extends ViewModel {
    MutableLiveData<RegisterErrorType> registerError = new MutableLiveData<>(null);
    MutableLiveData<String> registeredEmail = new MutableLiveData<>(null);
    MutableLiveData<String> pendingVerificationEmail = new MutableLiveData<>(null);

    public LiveData<RegisterErrorType> getErrorMessage() {
        return registerError;
    }
    public LiveData<String> getRegisteredEmail() {
        return registeredEmail;
    }
    public LiveData<String> getPendingVerificationEmail() {
        return pendingVerificationEmail;
    }

    public void registerUser(String email, String password, String name, String phone) {
        DataRepository.getInstance().register(email, password, name, phone, new RegisterCallback() {
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

        DataRepository.getInstance().sendVerificationEmail(new EmailVerificationCallback() {
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

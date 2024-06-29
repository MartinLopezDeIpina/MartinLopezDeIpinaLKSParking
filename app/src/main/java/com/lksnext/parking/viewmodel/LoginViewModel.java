package com.lksnext.parking.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parking.data.DataRepository;
import com.lksnext.parking.data.LoginErrorType;
import com.lksnext.parking.data.callbacks.LoginCallback;

public class LoginViewModel extends ViewModel {

    private DataRepository dataRepository;
    MutableLiveData<Boolean> logged = new MutableLiveData<>(null);
    MutableLiveData<LoginErrorType> loginError = new MutableLiveData<>(null);

    public LoginViewModel(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }
    public LoginViewModel() {
        this(DataRepository.getInstance());
    }


    public LiveData<Boolean> isLogged(){
        return logged;
    }
    public LiveData<LoginErrorType> getErrorMessage(){
        return loginError;
    }

    public void loginUser(String email, String password) {
        dataRepository.login(email, password, new LoginCallback() {
            @Override
            public void onSuccess() {
                logged.setValue(Boolean.TRUE);
            }

            //En caso de que el login sea incorrecto, que se hace
            @Override
            public void onFailure(LoginErrorType error) {
                loginError.setValue(error);
                logged.setValue(Boolean.FALSE);
            }
        });
    }
}


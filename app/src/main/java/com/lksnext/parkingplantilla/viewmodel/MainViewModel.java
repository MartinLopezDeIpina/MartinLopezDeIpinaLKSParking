package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.domain.Usuario;

public class MainViewModel extends ViewModel {
    // Aquí puedes declarar los LiveData y métodos necesarios para la vista main
    private final MutableLiveData<Usuario> user = new MutableLiveData<>(new Usuario("Martín", "López", "martinsaski@gmail.com", "123", "123123123"));

    public LiveData<Usuario> getUser() {
        return user;
    }

}

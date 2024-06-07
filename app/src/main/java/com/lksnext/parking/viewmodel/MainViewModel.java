package com.lksnext.parking.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.Usuario;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    List<Reserva> listaReservas = new ArrayList<>();

    public void setUser(){

    }


    private final MutableLiveData<Usuario> user = new MutableLiveData<>(new Usuario("1","Martín",  "martinsaski@gmail.com", "123123123"));

    public LiveData<Usuario> getUser() {
        return user;
    }
    public LiveData<String> getTipoPlaza(){
        return new MutableLiveData<>("Coche");
    }
    public LiveData<String> getIcono(){
        return new MutableLiveData<>("icono");
    }

}

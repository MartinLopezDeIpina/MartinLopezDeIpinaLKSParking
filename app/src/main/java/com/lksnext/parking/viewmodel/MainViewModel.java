package com.lksnext.parking.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.data.UserCallback;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.Usuario;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private DataBaseManager dataBaseManager = DataBaseManager.getInstance();
    List<Reserva> listaReservas = new ArrayList<>();

    public void setCurrentUserViewModel(){
        dataBaseManager.setCurrenUser(new UserCallback() {
            @Override
            public void onCallback(Usuario usuario) {
                user.setValue(usuario);
            }
        });
    }


    private final MutableLiveData<Usuario> user = new MutableLiveData<>(null);

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

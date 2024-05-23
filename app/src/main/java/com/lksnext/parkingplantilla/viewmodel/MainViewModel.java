package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.domain.TipoPlaza;
import com.lksnext.parkingplantilla.domain.Usuario;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    // Aquí puedes declarar los LiveData y métodos necesarios para la vista main
    List<Reserva> listaReservas = new ArrayList<>();


    private final MutableLiveData<Usuario> user = new MutableLiveData<>(new Usuario("Martín", "López", "martinsaski@gmail.com", "123", "123123123", null, null));

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

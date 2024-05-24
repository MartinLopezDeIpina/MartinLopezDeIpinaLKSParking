package com.lksnext.parking.view.activity;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parking.databinding.ActivityRegisterBinding;
import com.lksnext.parking.viewmodel.RegisterViewModel;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos la vista/interfaz de registro
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Asignamos el viewModel de register
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }
}
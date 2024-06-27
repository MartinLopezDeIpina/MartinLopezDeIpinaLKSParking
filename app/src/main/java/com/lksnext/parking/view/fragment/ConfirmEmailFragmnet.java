package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentConfirmEmailBinding;
import com.lksnext.parking.viewmodel.RegisterViewModel;

public class ConfirmEmailFragmnet extends Fragment {
    RegisterViewModel registerViewModel;
    private FragmentConfirmEmailBinding binding;
    private ImageView botonAtras;
    private Button botonAceptar;
    private NavController navController;
    public ConfirmEmailFragmnet() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = com.lksnext.parking.databinding.FragmentConfirmEmailBinding.inflate(inflater, container, false);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);


        bindButtonAtras();
        bindButtonAceptar();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void bindButtonAtras() {
        botonAtras = binding.returning;
        botonAtras.setOnClickListener(v -> navController.navigate(R.id.login_fragment));
    }
    private void bindButtonAceptar() {
        botonAceptar = binding.aceptarButton;
        botonAceptar.setOnClickListener(v -> navController.navigate(R.id.login_fragment));
    }
}

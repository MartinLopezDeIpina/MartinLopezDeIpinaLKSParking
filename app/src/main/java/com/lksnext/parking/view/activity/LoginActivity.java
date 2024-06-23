package com.lksnext.parking.view.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.util.DataBaseFiller;
import com.lksnext.parking.viewmodel.LoginViewModel;
import com.lksnext.parking.R;
import com.lksnext.parking.databinding.ActivityLoginBinding;
import com.lksnext.parking.view.fragment.RegisterFragment;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBaseFiller dataBaseFiller = new DataBaseFiller();
        //dataBaseFiller.fillReservas();
        //dataBaseFiller.fillPlaza98();
        //dataBaseFiller.fillPlazas75_94();
        //dataBaseFiller.fillPlazasPruebasHoras();
        dataBaseFiller.fillReservasPasadas();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

}
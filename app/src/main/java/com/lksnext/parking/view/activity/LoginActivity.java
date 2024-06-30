package com.lksnext.parking.view.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.widget.Toast;

import com.lksnext.parking.util.DataBaseFiller;
import com.lksnext.parking.util.notifications.NotificationsManager;
import com.lksnext.parking.viewmodel.LoginViewModel;
import com.lksnext.parking.R;
import com.lksnext.parking.databinding.ActivityLoginBinding;

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
        //dataBaseFiller.fillReservasPasadas();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

}
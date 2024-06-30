package com.lksnext.parking.view.activity;

import static android.icu.number.NumberRangeFormatter.with;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.widget.Toast;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Calendar calendar = Calendar.getInstance();
            // 10 is for how many seconds from now you want to schedule also you can create a custom instance of Callender to set on exact time
            calendar.add(Calendar.SECOND, 5);
            // function for creating Notification Channel
            createNotificationChannel();
            // function for scheduling the notification
            scheduleNotification(calendar);
        }


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "canal1";
            String description = "descripcion canal1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("canal1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(Calendar calendar){
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("titleExtra", "Dynamic Title");
        intent.putExtra("textExtra", "Dynamic Text Body");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(getApplicationContext(), "Scheduled ", Toast.LENGTH_LONG).show();
    }

    private void notification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "canal1";
            String description = "descripcion canal1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("canal1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal1")
                .setSmallIcon(R.drawable.reservacheckedicon)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that fires when the user taps the notification.
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

// notificationId is a unique int for each notification that you must define.
        notificationManager.notify(0, builder.build());
    }


}
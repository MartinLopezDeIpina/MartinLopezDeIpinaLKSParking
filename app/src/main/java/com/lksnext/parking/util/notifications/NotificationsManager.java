package com.lksnext.parking.util.notifications;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.util.DateUtils;

import java.util.Date;
import java.util.UUID;

public class NotificationsManager {

    public static void scheduleBookingNotification(Reserva reserva, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Reserva reservaPrueba = new Reserva("2024-06-30", "2", Long.valueOf(3), new Hora("12:00", "13:00"), true, TipoPlaza.COCHE);

            String horaInicio = reservaPrueba.getHora().getHoraInicio();
            String date = reservaPrueba.getFecha();
            Date horaInicioDate = DateUtils.parseStringDateAndHour(date, horaInicio);

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(horaInicioDate);
            calendar1.add(Calendar.MINUTE, -2);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(horaInicioDate);
            calendar2.add(Calendar.MINUTE, -1);

            createNotificationChannel(context);

            scheduleNotification(calendar1, context);
            scheduleNotification(calendar2, context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "canal1";
            String description = "descripcion canal1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("canal1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private static void scheduleNotification(Calendar calendar, Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("titleExtra", "Dynamic Title");
        intent.putExtra("textExtra", "Dynamic Text Body");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Scheduled ", Toast.LENGTH_LONG).show();
    }
}

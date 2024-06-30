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

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.domain.Notificacion;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.util.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class NotificationsManager {

    public static void scheduleBookingNotification(Reserva reserva, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String horaInicio = reserva.getHora().getHoraInicio();
            String date = reserva.getFecha();
            Date horaInicioDate = DateUtils.parseStringDateAndHour(date, horaInicio);

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(horaInicioDate);
            calendar1.add(Calendar.MINUTE, -30);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(horaInicioDate);
            calendar2.add(Calendar.MINUTE, -15);

            createNotificationChannel(context);

            scheduleNotification(calendar1, context, reserva);
            scheduleNotification(calendar2, context, reserva);
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
    private static void scheduleNotification(Calendar calendar, Context context, Reserva reserva) {
        int requestCode = UUID.randomUUID().hashCode();

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("titleExtra", "Recordatorio de reserva");
        String notificacionMensaje = String.format("Hey, recuerda que tienes una reserva de %s a %s en la plaza nº%s", reserva.getHora().getHoraInicio(), reserva.getHora().getHoraFin(), reserva.getPlazaID());
        intent.putExtra("textExtra", notificacionMensaje);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        DataBaseManager.addNotificationToDB(reserva.getId(), requestCode);
    }

    public static void cancelBookingNotifications(String reservaID, LifecycleOwner lifecycleOwner, Context context){
        LiveData<List<Notificacion>> notifications = DataBaseManager.getAndDeleteNotificationsFromDB(reservaID);
        notifications.observe(lifecycleOwner, notificaciones -> {
            if(notificaciones != null){
                for (Notificacion notificacion : notificaciones)
                    cancelNotification(context, notificacion.getRequestCode());
            }
        });
    }

    private static void cancelNotification(Context context, int requestCode) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    //para debug
    public static void removeAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}

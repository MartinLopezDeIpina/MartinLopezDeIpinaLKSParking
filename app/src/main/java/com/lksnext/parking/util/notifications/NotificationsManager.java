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

import com.lksnext.parking.domain.Reserva;

public class NotificationsManager {

    public static void scheduleBookingNotification(Reserva reserva, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Calendar calendar = Calendar.getInstance();
            // 10 is for how many seconds from now you want to schedule also you can create a custom instance of Callender to set on exact time
            calendar.add(Calendar.SECOND, 5);
            // function for creating Notification Channel
            createNotificationChannel(context);
            // function for scheduling the notification
            scheduleNotification(calendar, context);
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Scheduled ", Toast.LENGTH_LONG).show();
    }
}

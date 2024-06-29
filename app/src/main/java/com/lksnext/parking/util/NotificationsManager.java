package com.lksnext.parking.util;


import static android.provider.Settings.System.getString;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.lksnext.parking.R;

public class NotificationsManager {

    static String CHANNEL_ID = "channel_01";
    static String CHANNEL_NAME = "Reservas notificaciones";
    static String CHANNEL_DESCRIPTION = "Para mandar notificaciones de reserva activa";


    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

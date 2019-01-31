package com.artem.bakuta.logger.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.artem.bakuta.logger.R;
import com.artem.bakuta.logger.activity.LoggerActivity;

public class LoggerService extends Service {
    NotificationManager nm;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, LoggerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else {

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.baseline_gesture_black_24dp)
                    .setContentTitle("Logger is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pIntent)
                    .build();

            startForeground(2, notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {

        Intent intent = new Intent(this, LoggerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String NOTIFICATION_CHANNEL_ID = "com.artem.bakuta.logger";
        String channelName = "Logger Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.baseline_gesture_black_24dp)
                .setContentTitle("Logger is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pIntent)
                .build();
        startForeground(2, notification);
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
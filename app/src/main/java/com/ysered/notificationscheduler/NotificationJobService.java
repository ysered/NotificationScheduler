package com.ysered.notificationscheduler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;


public class NotificationJobService extends JobService {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private NotificationManager notificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {
        createNotificationChannel();

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this, 0,
                mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Job Service")
                .setContentText("Your Job is running!")
                .setContentIntent(mainActivityPendingIntent)
                .setSmallIcon(R.drawable.ic_job_running)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build();

        notificationManager.notify(0, notification);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Job Service notificaiton", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Channel from Job Service");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}

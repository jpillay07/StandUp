package com.codelabs.standup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.system.Os;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    ToggleButton alarmToggle;
    NotificationManager mNotificationManager;
    AlarmManager alarmManager;
    PendingIntent notifyPendingIntent;
    boolean alarmActive;

    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "Primary_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        alarmActive = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);

        notifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        alarmToggle = findViewById(R.id.alarmToggle);
        alarmToggle.setChecked(alarmActive);

        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(alarmManager != null){
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                                SystemClock.elapsedRealtime() + 2000,
                                2000, notifyPendingIntent);
                    }

                    Toast.makeText(MainActivity.this, R.string.alarm_on_toast, Toast.LENGTH_SHORT).show();
            }
                else{
                    if(alarmManager != null){
                        alarmManager.cancel(notifyPendingIntent);
                    }
                    Toast.makeText(MainActivity.this, R.string.alarm_off_toast, Toast.LENGTH_SHORT).show();
                    mNotificationManager.cancelAll();
                }
            }
        });

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Stand Up Notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setDescription("Notifies user to get up every 15 minutes and walk");

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }


}

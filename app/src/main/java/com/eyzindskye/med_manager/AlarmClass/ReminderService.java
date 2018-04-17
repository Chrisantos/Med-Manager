package com.eyzindskye.med_manager.AlarmClass;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.eyzindskye.med_manager.MainActivity;
import com.eyzindskye.med_manager.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.BaseColumns._ID;

/**
 * Created by CHRISANTUS EZE on 4/14/2018.
 */

public class ReminderService extends WakeReminderIntentService {
    Notification myNotication;


    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Log.d("ReminderService", "Doing work.");
        Long rowId = intent.getExtras().getLong(_ID);

        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(_ID, rowId);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.logo);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setAutoCancel(false);
        builder.setTicker("this is ticker text");
        builder.setContentTitle("MedManager");
        builder.setContentText("Time to take your medication");
        builder.setSmallIcon(R.drawable.logo);
        builder.setLargeIcon(icon);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("Meds");   //API level 16
        builder.setNumber(100);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        builder.build();

        myNotication = builder.getNotification();
        mgr.notify(11, myNotication);



    }


}

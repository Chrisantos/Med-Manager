package com.eyzindskye.med_manager.AlarmClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

import static android.provider.BaseColumns._ID;


/**
 * Created by CHRISANTUS EZE on 4/14/2018.
 */

public class OnAlarmReceiver extends BroadcastReceiver{
    private static final String TAG = ComponentInfo.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received wake up from alarm manager.");

        long rowid = intent.getExtras().getLong(_ID);

        WakeReminderIntentService.acquireStaticLock(context);

        Intent intent1 = new Intent(context, ReminderService.class);
        intent1.putExtra(_ID, rowid);
        context.startService(intent1);


    }
}

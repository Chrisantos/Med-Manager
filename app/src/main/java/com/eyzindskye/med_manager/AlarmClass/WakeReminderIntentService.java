package com.eyzindskye.med_manager.AlarmClass;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.annotation.Nullable;

/**
 * Created by CHRISANTUS EZE on 4/14/2018.
 */

public abstract class WakeReminderIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WakeReminderIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            doReminderWork(intent);
        }
        finally {
            getLock(this).release();
        }
    }


    abstract void doReminderWork(Intent intent);

    public static final String LOCK_NAME_STATIC="com.dummies.android.taskreminder.Static";
    private static PowerManager.WakeLock lockStatic=null;

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic==null) {
            PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
            lockStatic=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return(lockStatic);
    }

}

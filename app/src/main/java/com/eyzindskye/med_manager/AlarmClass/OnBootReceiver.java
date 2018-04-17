package com.eyzindskye.med_manager.AlarmClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eyzindskye.med_manager.AddMedication;
import com.eyzindskye.med_manager.HelperClass.DrugDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.provider.BaseColumns._ID;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.START_TIME;

/**
 * Created by CHRISANTUS EZE on 4/14/2018.
 */

public class OnBootReceiver extends BroadcastReceiver {
    private static final String TAG = ComponentInfo.class.getCanonicalName();
    DrugDB mDrugDB;
    SQLiteDatabase mDB;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            ReminderManager reminderMgr = new ReminderManager(context);

            mDrugDB = new DrugDB(context);
            mDB = mDrugDB.getReadableDatabase();

            Cursor cursor = mDrugDB.fetchAllReminders(mDB);

            if(cursor != null) {
                cursor.moveToFirst();

                int rowIdColumnIndex = cursor.getColumnIndex(_ID);
                int dateTimeColumnIndex = cursor.getColumnIndex(START_TIME);

                while(cursor.isAfterLast() == false) {

                    Log.d(TAG, "Adding alarm from boot.");
                    Log.d(TAG, "Row Id Column Index - " + rowIdColumnIndex);
                    Log.d(TAG, "Date Time Column Index - " + dateTimeColumnIndex);

                    Long rowId = cursor.getLong(rowIdColumnIndex);
                    String dateTime = cursor.getString(dateTimeColumnIndex);

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat(AddMedication.DATE_TIME_FORMAT);

                    try {
                        Date date = format.parse(dateTime);
                        cal.setTime(date);

                        reminderMgr.setReminder(rowId, cal);
                    } catch (java.text.ParseException e) {
                        Log.e("OnBootReceiver", e.getMessage(), e);
                    }

                    cursor.moveToNext();
                }
                cursor.close() ;
            }

            mDrugDB.close();
        }
    }
}

package com.eyzindskye.med_manager.HelperClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.provider.BaseColumns._ID;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.DESC;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.DURATION;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.INTERVAL;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.MONTH;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.NAME;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.QUANTITY;
import static com.eyzindskye.med_manager.HelperClass.DBContract.DBEntry.START_TIME;

/**
 * Created by CHRISANTUS EZE on 4/4/2018.
 */

public class DrugDB extends SQLiteOpenHelper {
    public static String TABLE_NAME = "med";
    public static String DATABASE = "medi.db";
    private static final int VERSION = 2;

    public DrugDB(Context context) {
        super(context, DATABASE, null , VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME + "( "+
                    _ID                + " INTEGER PRIMARY KEY, " +
                    NAME + " TEXT, " +
                    DBContract.DBEntry.DESC + " TEXT, " +
                    DBContract.DBEntry.MONTH + " TEXT, " +
                    DBContract.DBEntry.START_TIME+ " TEXT, " +
                    DBContract.DBEntry.DURATION+ " TEXT, " +
                    DBContract.DBEntry.INTERVAL + " TEXT, " +
                    DBContract.DBEntry.QUANTITY + " TEXT);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public long addMed(String name, String desc, String month, String start, String duration, String interval, String quantity, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(DESC, desc);
        values.put(MONTH, month);
        values.put(START_TIME, start);
        values.put(DURATION, duration);
        values.put(INTERVAL, interval);
        values.put(QUANTITY, quantity);

        return db.insert(TABLE_NAME, null, values);
    }

    public boolean deleteReminder(long rowId, SQLiteDatabase db) {

        return db.delete(TABLE_NAME, _ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllReminders(SQLiteDatabase db) {

        return db.query(TABLE_NAME, new String[] {_ID, NAME, DESC, MONTH, START_TIME, DURATION, INTERVAL, QUANTITY},
                null, null, null, null, null);

    }

    public Cursor fetchReminder(long rowId, SQLiteDatabase db) throws SQLException {


        Cursor mCursor =
                db.query(true, TABLE_NAME, new String[] {_ID, NAME, DESC, MONTH, START_TIME,
                                DURATION, INTERVAL, QUANTITY}, _ID + "=" + rowId, null,
                        null, null, null, null);

        if (mCursor !=null){
            if (mCursor.moveToFirst()){

            }
            mCursor.close();
        }



        return mCursor;

    }

    public Cursor fetchDrug(long rowId, SQLiteDatabase db ){
        return db.query(true, TABLE_NAME, new String[] {_ID, NAME, DESC, MONTH, START_TIME,
                        DURATION, INTERVAL, QUANTITY}, _ID + "=" + rowId, null,
                null, null, null, null);
    }
}

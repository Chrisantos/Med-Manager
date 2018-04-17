package com.eyzindskye.med_manager.HelperClass;

import android.provider.BaseColumns;

/**
 * Created by CHRISANTUS EZE on 30/10/2017.
 */

public class DBContract {
    private DBContract(){}

    //public static final String AUTHORITY = "com.example.chrisantuseze.hadum";
    //public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    //public static final String PATH_TASKS = "tasks";

    public class DBEntry implements BaseColumns {
        public static final String NAME = "name";
        public static final String DESC = "desc";
        public static final String MONTH = "month";
        public static final String START_TIME = "start";
        public static final String DURATION = "end";
        public static final String INTERVAL = "interval";
        public static final String QUANTITY = "quantity";

    }
}

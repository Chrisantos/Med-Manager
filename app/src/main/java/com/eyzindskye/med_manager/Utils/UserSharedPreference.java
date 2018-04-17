package com.eyzindskye.med_manager.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by CHRISANTUS EZE on 4/11/2018.
 */

public class UserSharedPreference {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String NAME = "user";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    Context context;

    public UserSharedPreference(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(NAME, 0);
        editor = sharedPreferences.edit();
    }

    public void setName(String name){
        editor.putString(KEY_NAME, name);
        editor.apply();
    }
    public void setEmail(String email){
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public String getName(){
        return sharedPreferences.getString(KEY_NAME, "");
    }
    public String getEmail(){
        return sharedPreferences.getString(KEY_EMAIL, "");
    }
}

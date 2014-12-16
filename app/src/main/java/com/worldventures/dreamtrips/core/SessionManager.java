package com.worldventures.dreamtrips.core;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    private static final String PREFER_NAME = "DreamTripsYo";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String name, String email) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }


    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}

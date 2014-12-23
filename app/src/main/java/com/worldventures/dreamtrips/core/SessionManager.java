package com.worldventures.dreamtrips.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.worldventures.dreamtrips.core.model.Session;

public class SessionManager {

    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String KEY_DREAM_TOKEN = "KEY_DREAM_TOKEN";
    private static final String PREFER_NAME = "DreamTripsYo";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public Session getCurrentSession() {
        String json = pref.getString(KEY_CURRENT_USER, "");
        return new Gson().fromJson(json, Session.class);
    }

    public void createUserLoginSession(Session session) {
        editor.putString(KEY_CURRENT_USER, new Gson().toJson(session));
        editor.commit();
    }

    public void createDreamToken(String token) {
        editor.putString(KEY_DREAM_TOKEN, token);
        editor.commit();
    }


    public void logoutUser() {
        editor.clear();
        editor.commit();
    }


    public boolean isUserLoggedIn() {
        return pref.contains(KEY_CURRENT_USER) && pref.contains(KEY_DREAM_TOKEN);
    }
}

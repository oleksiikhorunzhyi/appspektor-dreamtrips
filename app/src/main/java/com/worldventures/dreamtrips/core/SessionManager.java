package com.worldventures.dreamtrips.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.worldventures.dreamtrips.core.model.User;

public class SessionManager {


    public static final String KEY_TOKEN = "KEY_TOKEN";
    public static final String KEY_DREAM_TOKEN = "KEY_DREAM_TOKEN";
    public static final String KEY_USER = "KEY_USER";
    public static final String KEY_URL_FAQ = "KEY_URL_FAQ";
    public static final String KEY_URL_TERMS = "KEY_URL_TERMS";
    private static final String TERMS = "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/faq.html";
    private static final String FAQ = "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/terms_of_service.html";
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

    public String getCurrentSession() {
        return pref.getString(KEY_TOKEN, "");
    }

    public void createUserLoginSession(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getDreamToken() {
        return pref.getString(KEY_DREAM_TOKEN, "");
    }

    public void createDreamToken(String token) {
        editor.putString(KEY_DREAM_TOKEN, token);
        editor.commit();
    }

    public void saveCurrentUser(User user) {
        editor.putString(KEY_USER, new Gson().toJson(user));
        editor.commit();
    }

    public User getCurrentUser() {
        return new Gson().fromJson(pref.getString(KEY_USER, ""), User.class);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }


    public boolean isUserLoggedIn() {
        return pref.contains(KEY_TOKEN) && pref.contains(KEY_DREAM_TOKEN);
    }

    public void setTermsUrl(String termsUrl) {
        editor.putString(KEY_URL_TERMS, termsUrl);
        editor.commit();
    }

    public String getTermUrl() {
        return pref.getString(KEY_URL_TERMS, TERMS);
    }

    public String getFaqUrl() {
        return pref.getString(KEY_URL_FAQ, FAQ);
    }

    public void setFaqUrl(String faqUrl) {
        editor.putString(KEY_URL_FAQ, faqUrl);
        editor.commit();
    }


}

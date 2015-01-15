package com.worldventures.dreamtrips.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.worldventures.dreamtrips.core.model.User;

import de.greenrobot.event.EventBus;

public class SessionManager {


    public static final String KEY_TOKEN = "KEY_TOKEN";
    public static final String KEY_DREAM_TOKEN = "KEY_DREAM_TOKEN";
    public static final String KEY_USER = "KEY_USER";
    public static final String KEY_URL_FAQ = "KEY_URL_FAQ";
    public static final String KEY_URL_TERMS = "KEY_URL_TERMS";
    private static final String TERMS = "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/faq.html";
    private static final String FAQ = "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/terms_of_service.html";
    private static final String PREFER_NAME = "DreamTripsYo";


    public static class LogoutEvent {

    }

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;
    private final EventBus eventBus;
    private final int PRIVATE_MODE = 0;

    public SessionManager(Context context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
        this.pref = this.context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        this.editor = pref.edit();
    }

    public String getCurrentSession() {
        return pref.getString(KEY_TOKEN, "");
    }

    public void createUserLoginSession(String token) {
        this.editor.putString(KEY_TOKEN, token);
        this.editor.commit();
    }

    public String getDreamToken() {
        return this.pref.getString(KEY_DREAM_TOKEN, "");
    }

    public void createDreamToken(String token) {
        this.editor.putString(KEY_DREAM_TOKEN, token);
        this.editor.commit();
    }

    public void saveCurrentUser(User user) {
        this.editor.putString(KEY_USER, new Gson().toJson(user));
        this.editor.commit();
    }

    public User getCurrentUser() {
        return new Gson().fromJson(this.pref.getString(KEY_USER, ""), User.class);
    }

    public void logoutUser() {
        this.editor.clear();
        this.editor.commit();
        this.eventBus.post(new LogoutEvent());
    }


    public boolean isUserLoggedIn() {
        return this.pref.contains(KEY_TOKEN) && this.pref.contains(KEY_DREAM_TOKEN);
    }

    public void setTermsUrl(String termsUrl) {
        this.editor.putString(KEY_URL_TERMS, termsUrl);
        this.editor.commit();
    }

    public String getTermUrl() {
        return this.pref.getString(KEY_URL_TERMS, TERMS);
    }

    public String getFaqUrl() {
        return this.pref.getString(KEY_URL_FAQ, FAQ);
    }

    public void setFaqUrl(String faqUrl) {
        this.editor.putString(KEY_URL_FAQ, faqUrl);
        this.editor.commit();
    }


}

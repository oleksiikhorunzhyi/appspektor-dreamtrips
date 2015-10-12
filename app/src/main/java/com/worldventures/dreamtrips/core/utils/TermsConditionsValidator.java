package com.worldventures.dreamtrips.core.utils;

import android.content.SharedPreferences;

public class TermsConditionsValidator {

    private static final int TERMS_VERSION = 1;
    private static final String TERMS_ACCEPTED = "terms_accepted_ver_";
    private static final String NEW_TERMS_ACCEPTED = TERMS_ACCEPTED + TERMS_VERSION;

    private SharedPreferences sharedPreferences;

    public TermsConditionsValidator(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setNewVersionAccepted(boolean newVersionAccept) {
        sharedPreferences.edit().putBoolean(NEW_TERMS_ACCEPTED, newVersionAccept).apply();
    }

    public boolean newVersionAccepted() {
        return sharedPreferences.getBoolean(NEW_TERMS_ACCEPTED, false);
    }

    public void clearPreviousAcceptedTerms() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove("terms_accepted");
        for (int i = 1; i < TERMS_VERSION; i++) {
            prefsEditor.remove(TERMS_ACCEPTED + i);
        }
        prefsEditor.apply();
    }
}

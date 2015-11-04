package com.worldventures.dreamtrips.core.utils;


import android.util.Log;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Locale;

public class AccountHelper {

    public static Locale getAccountLocale(User user) {
        if (user.getLocale() == null) return null;
        String[] args = user.getLocale().split("-"); // e.g. en-us
        return new Locale(args[0], args[1]);
    }

}

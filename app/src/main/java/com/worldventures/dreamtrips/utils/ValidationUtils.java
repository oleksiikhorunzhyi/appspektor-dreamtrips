package com.worldventures.dreamtrips.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final String USERNAME_PATTERN = "^[a-z0-9._-]{2,25}$";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";


    public static VResult isUsernameValid(String username) {

        if (TextUtils.isEmpty(username)) {
            return new VResult(false, "Username is empty");
        }
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches()) {
            return new VResult(false, "Username is not valid");
        }
        return new VResult(true, null);
    }

    public static VResult isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        if (TextUtils.isEmpty(password)) {
            return new VResult(false, "Password is empty");
        }
        if (!matcher.matches()) {
            return new VResult(false, "Password is not valid");
        }
        return new VResult(true, null);
    }


    public static class VResult {
        boolean isValid;
        String message;

        private VResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getMessage() {
            return message;
        }
    }


}

package com.worldventures.dreamtrips.utils;

import android.text.TextUtils;

import com.worldventures.dreamtrips.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final String USERNAME_PATTERN = "^[a-z0-9._-]{2,25}$";
    private static final String PASSWORD_PATTERN = ".+";


    public static VResult isUsernameValid(String username) {

        if (TextUtils.isEmpty(username)) {
            return new VResult(false, R.string.username_empty);
        }
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches()) {
            return new VResult(false, R.string.username_not_valid);
        }
        return new VResult(true, 0);
    }

    public static VResult isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        if (TextUtils.isEmpty(password)) {
            return new VResult(false, R.string.password_empty);
        }
        if (!matcher.matches()) {
            return new VResult(false, R.string.password_not_valid);
        }
        return new VResult(true, 0);
    }

    public static boolean isPhotoTitleValid(String title){
        return !title.trim().isEmpty();
    }


    public static class VResult {
        boolean isValid;
        int messageRes;

        private VResult(boolean isValid, int message) {
            this.isValid = isValid;
            this.messageRes = message;
        }

        public boolean isValid() {
            return isValid;
        }

        public int getMessage() {
            return messageRes;
        }
    }


}

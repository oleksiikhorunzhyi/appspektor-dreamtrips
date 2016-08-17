package com.worldventures.dreamtrips.util;

import android.text.TextUtils;
import android.util.Patterns;

import com.techery.spares.utils.ValidationUtils.VResult;
import com.worldventures.dreamtrips.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

   private static final String USERNAME_PATTERN = ".+";
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

   public static VResult isEmailValid(String email) {
      boolean valid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
      return new VResult(valid, R.string.email_is_not_valid);
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

   public static boolean isUrl(String path) {
      return Patterns.WEB_URL.matcher(path).matches();
   }
}

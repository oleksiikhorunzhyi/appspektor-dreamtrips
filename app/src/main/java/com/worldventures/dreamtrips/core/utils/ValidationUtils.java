package com.worldventures.dreamtrips.core.utils;

import android.support.annotation.Nullable;
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

    public static boolean isPhotoTitleValid(String title) {
        return !title.trim().isEmpty();
    }


    public static class VResult {
        protected boolean isValid;
        protected int messageRes;

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

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new IllegalArgumentException("reference is null");
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference            an object reference
     * @param errorMessageTemplate a template for the exception message should the check fail. The
     *                             message is formed by replacing each {@code %s} placeholder in the template with an
     *                             argument. These are matched by position - the first {@code %s} gets {@code
     *                             errorMessageArgs[0]}, etc.  Unmatched arguments will be appended to the formatted message
     *                             in square braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments
     *                             are converted to strings using {@link String#valueOf(Object)}.
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference,
                                     @Nullable String errorMessageTemplate,
                                     @Nullable Object... errorMessageArgs) {
        if (reference == null) {
            // If either of these parameters is null, the right thing happens anyway
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    // Note that this is somewhat-improperly used from Verify.java as well.
    static String format(String template, @Nullable Object... args) {
        template = String.valueOf(template); // null -> "null"

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i]);
            i++;
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i]);
            i++;
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i]);
                i++;
            }
            builder.append(']');
        }

        return builder.toString();
    }
}

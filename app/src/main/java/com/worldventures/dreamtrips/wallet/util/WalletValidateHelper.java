package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.regex.Pattern;

public class WalletValidateHelper {

    public static boolean validateCardName(String cardName) {
        return Pattern.compile("[a-zA-Z ]{2,256}").matcher(cardName).matches();
    }

    public static void validateCardNameOrThrow(String cardName) throws FormatException {
        if (!validateCardName(cardName)) {
            throw new FormatException();
        }
    }

    public static boolean validateUserFullName(@NonNull String firstName, @Nullable String middleName, @NonNull String lastName) {
        Pattern pattern = Pattern.compile("[A-Za-z]{2,21}+");
        boolean result = pattern.matcher(firstName).matches() && pattern.matcher(lastName).matches();
        return result && (middleName == null || pattern.matcher(middleName).matches());
    }

    public static void validateUserFullNameOrThrow(
            @NonNull String firstName, @Nullable String middleName, @NonNull String lastName) throws FormatException {
        if (!validateUserFullName(firstName, middleName, lastName)) {
            throw new FormatException();
        }
    }

    public static void validateSCIdOrThrow(String scid) throws FormatException {
        if (!validateSCId(scid)) {
            throw new FormatException();
        }
    }

    public static boolean validateSCId(String scid) {
        return scid != null && !scid.isEmpty();
    }
}

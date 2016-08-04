package com.worldventures.dreamtrips.wallet.util;

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

    public static boolean validateUserFullName(String cardName) {
        return Pattern.compile("[A-Za-z]{2,21}+ [A-Za-z]{2,21}+").matcher(cardName).matches();
    }

    public static void validateUserFullNameOrThrow(String cardName) throws FormatException {
        if (!validateUserFullName(cardName)) {
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

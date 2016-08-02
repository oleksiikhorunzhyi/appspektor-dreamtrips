package com.worldventures.dreamtrips.wallet.util;

import java.util.regex.Pattern;

public class CardValidateHelper {

    public static boolean validateCardName(String cardName) {
        return Pattern.compile("[a-zA-Z ]{2,256}").matcher(cardName).matches();
    }

    public static void validateCardNameOrThrow(String cardName) throws FormatException {
        if (!validateCardName(cardName)) {
            throw new FormatException();
        }
    }


}

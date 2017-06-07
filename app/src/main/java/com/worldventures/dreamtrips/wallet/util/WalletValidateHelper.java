package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

public class WalletValidateHelper {

   private static final Pattern CARD_NAME_PATTERN = Pattern.compile("^[ -a-zA-Z0-9]((\\s|-)*[a-zA-Z0-9- ])*$");
   private static final Pattern FIRST_NAME_PATTERN = Pattern.compile("^[\\p{L}]{2,21}+");
   private static final Pattern MIDDLE_NAME_PATTERN = Pattern.compile("^[\\p{L}]{0,21}+");
   private static final Pattern LAST_NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,21}+");
   private static final Pattern SCID_PATTERN = Pattern.compile("^\\d+$");

   public static void validateUserFullNameOrThrow(@NonNull String firstName, @NonNull String middleName, @NonNull String lastName) throws FormatException {
      if (!FIRST_NAME_PATTERN.matcher(firstName).matches()) {
         throw new FirstNameException();
      }
      if (!MIDDLE_NAME_PATTERN.matcher(middleName).matches()) {
         throw new MiddleNameException();
      }
      if (!LAST_NAME_PATTERN.matcher(lastName).matches()) {
         throw new LastNameException();
      }
   }

   public static void validateSCIdOrThrow(String scid) throws FormatException {
      if (!validateSCId(scid)) {
         throw new FormatException(String.format("Wrong scID: %s", scid));
      }
   }

   public static boolean validateCardCvv(String cvv, String cardNumber) {
      return cvv.length() == WalletRecordUtil.obtainRequiredCvvLength(cardNumber);
   }

   public static boolean validateSCId(String scid) {
      return SCID_PATTERN.matcher(scid).matches();
   }

   public static void validateCardNameOrThrow(String cardName) throws CardNameFormatException {
      if (!isValidCardName(cardName)) throw new CardNameFormatException();
   }

   public static boolean isValidCardName(String cardName) {
      final int length = cardName.length();
      return length > 0 && length <= 11 && CARD_NAME_PATTERN.matcher(cardName).matches();
   }

   public static void validateCvvOrThrow(String cvv, String cardNumber) throws CvvFormatException {
      if (!validateCardCvv(cvv, cardNumber)) throw new CvvFormatException();
   }
}

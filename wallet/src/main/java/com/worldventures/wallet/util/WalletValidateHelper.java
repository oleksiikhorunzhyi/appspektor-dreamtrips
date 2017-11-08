package com.worldventures.wallet.util;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

public final class WalletValidateHelper {

   private static final Pattern CARD_NAME_PATTERN = Pattern.compile("^[\\-a-zA-Z0-9](?:(?:\\s|-)*[\\-a-zA-Z0-9\\s])*$");
   private static final Pattern FIRST_NAME_PATTERN = Pattern.compile("^\\s*[a-zA-Z][a-zA-Z\\-]{0,19}[a-zA-Z]\\s*$");
   private static final Pattern MIDDLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z]{0,21}+");
   private static final Pattern LAST_NAME_PATTERN = Pattern.compile("^\\s*[a-zA-Z][a-zA-Z\\-\\s]*[a-zA-Z]\\.?\\s*$");
   private static final Pattern SCID_PATTERN = Pattern.compile("^\\d+$");

   private WalletValidateHelper() {
   }

   public static void validateUserFullNameOrThrow(@NonNull String firstName, @NonNull String middleName, @NonNull String lastName) throws FormatException {
      if (!isValidFirstName(firstName)) {
         throw new FirstNameException();
      }
      if (!MIDDLE_NAME_PATTERN.matcher(middleName).matches()) {
         throw new MiddleNameException();
      }
      if (!isValidLastName(lastName)) {
         throw new LastNameException();
      }
   }

   public static boolean isValidFirstName(String firstName) {
      return FIRST_NAME_PATTERN.matcher(firstName).matches();
   }

   public static boolean isValidLastName(String lastName) {
      if (lastName == null || lastName.length() > 21 || lastName.length() < 2) {
         return false;
      }
      return LAST_NAME_PATTERN.matcher(lastName).matches();
   }

   public static void validateSCIdOrThrow(String scid) throws FormatException {
      if (!validateSCId(scid)) {
         throw new FormatException(String.format("Wrong scID: %s", scid));
      }
   }

   public static boolean validateCardCvv(String cvv, String cardNumber) {
      return cvv.length() == WalletRecordUtil.Companion.obtainRequiredCvvLength(cardNumber);
   }

   public static boolean validateSCId(String scid) {
      return SCID_PATTERN.matcher(scid).matches();
   }

   public static void validateCardNameOrThrow(String cardName) throws CardNameFormatException {
      if (!isValidCardName(cardName)) {
         throw new CardNameFormatException();
      }
   }

   public static boolean isValidCardName(String cardName) {
      final int length = cardName.length();
      return length > 0 && length <= 11 && CARD_NAME_PATTERN.matcher(cardName).matches();
   }

   public static void validateCvvOrThrow(String cvv, String cardNumber) throws CvvFormatException {
      if (!validateCardCvv(cvv, cardNumber)) {
         throw new CvvFormatException();
      }
   }
}

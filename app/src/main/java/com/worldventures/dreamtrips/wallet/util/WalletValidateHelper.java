package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

import java.util.regex.Pattern;

public class WalletValidateHelper {

   private static final Pattern CARD_NAME_PATTERN = Pattern.compile("^\\S([a-zA-Z0-9\\-\\s]{1,11}+)");
   private static final Pattern FIRST_NAME_PATTERN = Pattern.compile("[\\p{L}]{3,21}+");
   private static final Pattern MIDDLE_NAME_PATTERN = Pattern.compile("[\\p{L}]{0,21}+");
   private static final Pattern LAST_NAME_PATTERN = Pattern.compile("[a-zA-Z\\s]{3,21}+");
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

   public static boolean validateAddressInfo(AddressInfo addressInfo) {
      boolean infoInvalid = TextUtils.isEmpty(addressInfo.address1()) || TextUtils.isEmpty(addressInfo.city()) || TextUtils
            .isEmpty(addressInfo.state()) || TextUtils.isEmpty(addressInfo.zip());

      return !infoInvalid;
   }

   public static boolean validateCardCvv(String cvv, String cardNumber) {
      return cvv.length() == WalletRecordUtil.obtainRequiredCvvLength(cardNumber);
   }

   public static boolean validateSCId(String scid) {
      return SCID_PATTERN.matcher(scid).matches();
   }

   public static void validateCardNameOrThrow(String cardName) throws CardNameFormatException {
      if (!CARD_NAME_PATTERN.matcher(cardName).matches()) throw new CardNameFormatException();
   }

   public static boolean validateCardName(String cardName) {
      return CARD_NAME_PATTERN.matcher(cardName).matches();
   }

   public static void validateAddressInfoOrThrow(AddressInfo addressInfo) throws AddressFormatException {
      if (!validateAddressInfo(addressInfo)) {
         throw new AddressFormatException();
      }
   }

   public static void validateCvvOrThrow(String cvv, String cardNumber) throws CvvFormatException {
      if (!validateCardCvv(cvv, cardNumber)) throw new CvvFormatException();
   }
}

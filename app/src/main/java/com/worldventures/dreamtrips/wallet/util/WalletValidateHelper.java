package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

import java.util.regex.Pattern;

public class WalletValidateHelper {

   private static final Pattern CARD_NAME_PATTERN = Pattern.compile("[a-zA-Z ]{2,256}");
   private static final Pattern FULL_NAME_PATTERN = Pattern.compile("[A-Za-z]{2,21}+");
   private static final Pattern CVV_PATTERN = Pattern.compile("[0-9]{3,4}");

   public static boolean validateCardName(String cardName) {
      return CARD_NAME_PATTERN.matcher(cardName).matches();
   }

   public static void validateCardNameOrThrow(String cardName) throws FormatException {
      if (!validateCardName(cardName)) {
         throw new FormatException();
      }
   }

   public static boolean validateUserFullName(@NonNull String firstName, @Nullable String middleName, @NonNull String lastName) {
      boolean result = FULL_NAME_PATTERN.matcher(firstName).matches() && FULL_NAME_PATTERN.matcher(lastName).matches();
      return result && (middleName == null || FULL_NAME_PATTERN.matcher(middleName).matches());
   }

   public static void validateUserFullNameOrThrow(@NonNull String firstName, @Nullable String middleName, @NonNull String lastName) throws FormatException {
      if (!validateUserFullName(firstName, middleName, lastName)) {
         throw new FormatException();
      }
   }

   public static void validateSCIdOrThrow(String scid) throws FormatException {
      if (!validateSCId(scid)) {
         throw new FormatException();
      }
   }

   public static boolean validateAddressInfo(AddressInfo addressInfo) {
      boolean infoInvalid = TextUtils.isEmpty(addressInfo.address1()) || TextUtils.isEmpty(addressInfo.city()) || TextUtils
            .isEmpty(addressInfo.state()) || TextUtils.isEmpty(addressInfo.zip());

      return !infoInvalid;
   }

   public static boolean validateCardCvv(String cvv) {
      return CVV_PATTERN.matcher(cvv).matches();
   }

   public static boolean validateSCId(String scid) {
      return scid != null && !scid.isEmpty();
   }
}

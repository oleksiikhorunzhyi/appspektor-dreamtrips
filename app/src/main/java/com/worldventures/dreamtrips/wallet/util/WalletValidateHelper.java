package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

import java.util.regex.Pattern;

public class WalletValidateHelper {

   private static final Pattern FIRST_NAME_PATTERN = Pattern.compile("[\\p{L}]{2,21}+");
   private static final Pattern MIDDLE_NAME_PATTERN = Pattern.compile("[\\p{L}]{1,21}+");
   private static final Pattern LAST_NAME_PATTERN = FIRST_NAME_PATTERN;
   private static final Pattern SCID_PATTERN = Pattern.compile("^\\d+$");

   public static boolean validateUserFullName(@NonNull String firstName, @Nullable String middleName, @NonNull String lastName) {
      boolean result = FIRST_NAME_PATTERN.matcher(firstName).matches() && LAST_NAME_PATTERN.matcher(lastName).matches();
      return result && (middleName == null || MIDDLE_NAME_PATTERN.matcher(middleName).matches());
   }

   public static void validateUserFullNameOrThrow(@NonNull String firstName, @Nullable String middleName, @NonNull String lastName) throws FormatException {
      if (!validateUserFullName(firstName, middleName, lastName)) {
         throw new FormatException();
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

   public static boolean validateCardCvv(String cvv, long cardNumber) {
      return cvv.length() == BankCardHelper.obtainRequiredCvvLength(cardNumber);
   }

   public static boolean validateSCId(String scid) {
      return SCID_PATTERN.matcher(scid).matches();
   }
}

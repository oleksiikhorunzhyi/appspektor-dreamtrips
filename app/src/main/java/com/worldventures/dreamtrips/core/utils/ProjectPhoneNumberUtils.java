package com.worldventures.dreamtrips.core.utils;

import android.os.Build;
import android.telephony.PhoneNumberUtils;

public final class ProjectPhoneNumberUtils {

   private ProjectPhoneNumberUtils() {}

   public static String normalizeNumber(String phoneNumber) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         return PhoneNumberUtils.normalizeNumber(phoneNumber);
      }
      return phoneNumber;
   }

   @SuppressWarnings("deprecation")
   public static String formatNumber(String phoneNumber, String country) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         return PhoneNumberUtils.formatNumber(phoneNumber, country);
      }
      return PhoneNumberUtils.formatNumber(phoneNumber);
   }
}

package com.worldventures.dreamtrips.wallet.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeUtils {

   private static final String ISO_FORMAT_WITH_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss";
   private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat(ISO_FORMAT_WITH_TIMEZONE, Locale.US);

   public static String formatToIso(long millis) {
      return ISO_DATE_FORMAT.format(millis);
   }

}

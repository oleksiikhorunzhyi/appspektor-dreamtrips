package com.messenger.util;

public final class TruncateUtils {

   private TruncateUtils() {
   }

   public static String truncate(String source, int length) {
      if (source == null || source.length() <= length) {
         return source;
      }
      return source.substring(0, length - 1).concat("\u2026");
   }
}

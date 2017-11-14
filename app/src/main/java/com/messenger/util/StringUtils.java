package com.messenger.util;

public final class StringUtils {

   private StringUtils() {
   }

   public static boolean containsIgnoreCase(String source, String check) {
      return source.toLowerCase().contains(check.toLowerCase());
   }
}

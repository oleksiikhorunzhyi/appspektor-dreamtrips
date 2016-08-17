package com.messenger.util;

public class StringUtils {

   public static boolean containsIgnoreCase(String source, String check) {
      return source.toLowerCase().contains(check.toLowerCase());
   }
}

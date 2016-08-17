package com.messenger.util;

public class TruncateUtils {

   public static String truncate(String source, int length) {
      if (source == null || source.length() <= length) return source;
      return source.substring(0, length - 1).concat("\u2026");
   }
}

package com.worldventures.core.utils;

import org.joda.time.Period;

public class VideoDurationFormatter {

   public static String getFormattedDuration(long duration) {
      Period period = new Period(duration);
      int hours = period.getHours();
      int minutes = period.getMinutes();
      int seconds = period.getSeconds();

      if (seconds == 0) { return "0:00"; }

      StringBuilder sb = new StringBuilder();
      if (hours > 0) {
         sb.append(String.valueOf(hours)).append(":");
      }
      if (minutes > 0) {
         if (hours > 0 && minutes < 10) { sb.append("0"); }
         sb.append(String.valueOf(minutes)).append(":");
      }
      if (hours == 0 && minutes == 0) {
         sb.append("0:");
      }
      if (seconds > 0) {
         if (seconds < 10) { sb.append("0"); }
         sb.append(String.valueOf(seconds));
      }

      return sb.toString();
   }
}

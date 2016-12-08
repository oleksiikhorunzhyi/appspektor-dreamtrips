package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.util;

import android.content.Context;

import com.worldventures.dreamtrips.R;

public class UploadingTimeLeftFormatter {

   private static final String DURATION_FORMAT = "%d%s %d%s";

   private Context context;
   private String hoursString;
   private String minutesString;
   private String secondsString;

   public UploadingTimeLeftFormatter(Context context) {
      this.context = context;
      hoursString = context.getString(R.string.uploading_post_time_left_hours);
      minutesString = context.getString(R.string.uploading_post_time_left_minutes);
      secondsString = context.getString(R.string.uploading_post_time_left_seconds);
   }

   public String format(long durationMillis) {
      if (durationMillis == -1) {
         return context.getString(R.string.uploading_post_time_left_estimating);
      }
      long totalSecondsCount = durationMillis / 1000;
      long totalMinutesCount = totalSecondsCount / 60;
      long totalHoursCount = totalMinutesCount / 60;
      long totalDaysCount = totalHoursCount / 24;
      if (totalDaysCount > 0) {
         return context.getString(R.string.uploading_post_time_left_more_than_one_day);
      }
      long secondsRemainder = totalSecondsCount % 60;
      long minutesRemainder = totalMinutesCount % 60;

      if (totalHoursCount > 0) {
         long roundedMinutesRemainder = minutesRemainder;
         if (secondsRemainder > 30) roundedMinutesRemainder++;
         return String.format(DURATION_FORMAT, totalHoursCount, hoursString, roundedMinutesRemainder, minutesString);
      } else {
         return String.format(DURATION_FORMAT, minutesRemainder, minutesString, secondsRemainder, secondsString);
      }
   }
}

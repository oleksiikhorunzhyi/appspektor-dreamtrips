package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import timber.log.Timber;

public class CSTConverter {

   public CSTConverter() {
   }

   public String getCorrectTimeWrote(Context context, String timeWrote) throws ParseException {
      Calendar calendar = getCorrectTime(timeWrote);
      return String.valueOf(
            DateTimeUtils.getRelativeTimeSpanString(context.getResources(), calendar.getTimeInMillis())
      );
   }

   private Calendar getCorrectTime(@NonNull String dateToConvert) {
      SimpleDateFormat df = new SimpleDateFormat(DateTimeUtils.REVIEWS_DATE_FORMAT);
      df.setTimeZone(TimeZone.getTimeZone(DateTimeUtils.UTC));
      Date date;
      Calendar calendar = null;
      try {
         date = df.parse(dateToConvert);
         df.setTimeZone(TimeZone.getDefault());
         calendar = Calendar.getInstance();
         calendar.setTime(date);
      } catch (Exception e) {
         Timber.e(e.getMessage());
      }
      return calendar;
   }

   private int getDifferenceTime(@NonNull Calendar localTime, @NonNull Calendar commentTime, int typeToCompare) {
      return localTime.get(typeToCompare) - commentTime.get(typeToCompare);
   }
}

package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;

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

      String info = "";
      Calendar calendar = getCorrectTime(timeWrote);
      if (null != calendar) {
         Calendar localCalendar = Calendar.getInstance();
         Resources res = context.getResources();
         int time = getDifferenceTime(localCalendar, calendar, Calendar.YEAR);
         //Year
         if (time > 0) {
            if (time == 1) {
               info = String.format(res.getString(R.string.year_ago_text), time);
            } else {
               info = String.format(res.getString(R.string.years_ago_text), time);
            }

         } else {
            //Month
            time = getDifferenceTime(localCalendar, calendar, Calendar.MONTH);
            if (time > 0) {
               if (time == 1) {
                  info = String.format(res.getString(R.string.month_ago_text), time);
               } else {
                  info = String.format(res.getString(R.string.months_ago_text), time);
               }

            } else {
               //Week
               time = getDifferenceTime(localCalendar, calendar, Calendar.DAY_OF_WEEK_IN_MONTH);
               if (time > 0) {
                  if (time == 1) {
                     info = String.format(res.getString(R.string.week_ago_text), time);
                  } else {
                     info = String.format(res.getString(R.string.weeks_ago_text), time);
                  }
               } else {
                  //days
                  time = getDifferenceTime(localCalendar, calendar, Calendar.DAY_OF_MONTH);
                  if (time > 0) {
                     if (time == 1) {
                        info = String.format(res.getString(R.string.day_ago_text), time);
                     } else {
                        info = String.format(res.getString(R.string.days_ago_text), time);
                     }
                  } else {
                     //hours
                     time = getDifferenceTime(localCalendar, calendar, Calendar.HOUR_OF_DAY);
                     if (time > 0) {
                        if (time == 1) {
                           info = String.format(res.getString(R.string.hour_ago_text), time);
                        } else {
                           info = String.format(res.getString(R.string.hours_ago_text), time);
                        }
                     } else {
                        //min
                        time = getDifferenceTime(localCalendar, calendar, Calendar.MINUTE);
                        if (time > 0) {
                           info = String.format(res.getString(R.string.min_ago_text), time);
                        } else {
                           //seg
                           time = getDifferenceTime(localCalendar, calendar, Calendar.SECOND);
                           if (time > 0) {
                              info = String.format(res.getString(R.string.sec_ago_text), time);
                           }
                        }
                     }
                  }
               } //dia
            } //weeks
         } //month
      }
      return info;
   }

   private Calendar getCorrectTime(@NonNull String dateToConvert) {
      SimpleDateFormat df = new SimpleDateFormat(DateTimeUtils.REVIEWS_DATE_FORMAT);
      df.setTimeZone(TimeZone.getTimeZone(DateTimeUtils.UTC));
      Date date = null;
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

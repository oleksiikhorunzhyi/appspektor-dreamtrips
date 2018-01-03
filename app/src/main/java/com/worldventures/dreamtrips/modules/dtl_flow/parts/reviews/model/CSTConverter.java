package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;

import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class CSTConverter {

   public String getCorrectTimeWrote(Context context, String timeWrote) throws ParseException {

      String info = "";
      Calendar calendar = getCorrectTime(timeWrote);
      if (calendar == null) {
         return info;
      }

      Period period = new Period(calendar.getTimeInMillis(), Calendar.getInstance().getTimeInMillis());

      Resources res = context.getResources();
      //Year
      int timeUnit = period.getYears();
      if (timeUnit > 0) {
         if (timeUnit == 1) {
            info = String.format(res.getString(R.string.year_ago_text), timeUnit);
         } else {
            info = String.format(res.getString(R.string.years_ago_text), timeUnit);
         }
      } else {
         //Month
         timeUnit = period.getMonths();
         if (timeUnit > 0) {
            if (timeUnit == 1) {
               info = String.format(res.getString(R.string.month_ago_text), timeUnit);
            } else {
               info = String.format(res.getString(R.string.months_ago_text), timeUnit);
            }
         } else {
            //Week
            timeUnit = period.getWeeks();
            if (timeUnit > 0) {
               if (timeUnit == 1) {
                  info = String.format(res.getString(R.string.week_ago_text), timeUnit);
               } else {
                  info = String.format(res.getString(R.string.weeks_ago_text), timeUnit);
               }
            } else {
               //days
               timeUnit = period.getDays();
               if (timeUnit > 0) {
                  if (timeUnit == 1) {
                     info = String.format(res.getString(R.string.day_ago_text), timeUnit);
                  } else {
                     info = String.format(res.getString(R.string.days_ago_text), timeUnit);
                  }
               } else {
                  //hours
                  timeUnit = period.getHours();
                  if (timeUnit > 0) {
                     if (timeUnit == 1) {
                        info = String.format(res.getString(R.string.hour_ago_text), timeUnit);
                     } else {
                        info = String.format(res.getString(R.string.hours_ago_text), timeUnit);
                     }
                  } else {
                     //min
                     timeUnit = period.getMinutes();
                     if (timeUnit > 0) {
                        info = String.format(res.getString(R.string.min_ago_text), timeUnit);
                     } else {
                        //seg
                        timeUnit = period.getSeconds();
                        if (timeUnit > 0) {
                           info = String.format(res.getString(R.string.sec_ago_text), timeUnit);
                        }
                     }
                  }
               }
            } //dia
         } //weeks
      } //month

      return info;
   }

   private Calendar getCorrectTime(@NonNull String dateToConvert) {
      SimpleDateFormat df = new SimpleDateFormat(DateTimeUtils.REVIEWS_DATE_FORMAT, Locale.getDefault());
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
}

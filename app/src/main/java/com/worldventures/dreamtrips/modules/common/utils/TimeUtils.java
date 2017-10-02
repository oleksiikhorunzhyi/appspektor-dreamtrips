package com.worldventures.dreamtrips.modules.common.utils;


import android.content.Context;
import android.content.res.Resources;

import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Days;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

   private TimeUtils() {
   }

   public static CharSequence getRelativeTimeSpanString(Resources res, long startTime) {
      int deltaSeconds = (int) ((Calendar.getInstance().getTimeInMillis() - startTime) / 1000);
      int deltaMinutes = (int) Math.floor(deltaSeconds / 60);

      if (deltaSeconds < 60) {
         return res.getString(R.string.just_now);
      } else if (deltaSeconds < 120) {
         return res.getString(R.string.minute_ago);
      } else if (deltaMinutes < 60) {
         return res.getString(R.string.minutes_ago, deltaMinutes);
      } else if (deltaMinutes < 120) {
         return res.getString(R.string.hour_ago);
      } else if (deltaMinutes < (24 * 60)) {
         return res.getString(R.string.hours_ago, (int) Math.floor(deltaMinutes / 60));
      } else if (deltaMinutes < (24 * 60 * 2)) {
         return res.getString(R.string.yesterday);
      } else if (deltaMinutes < (24 * 60 * 7)) {
         return res.getString(R.string.days_ago, (int) Math.floor(deltaMinutes / (60 * 24)));
      } else if (deltaMinutes < (24 * 60 * 14)) {
         return res.getString(R.string.last_week);
      } else if (deltaMinutes < (24 * 60 * 31)) {
         return res.getString(R.string.weeks_ago, (int) Math.floor(deltaMinutes / (60 * 24 * 7)));
      } else if (deltaMinutes < (24 * 60 * 61)) {
         return res.getString(R.string.last_month);
      } else if (deltaMinutes < (24 * 60 * 365.25)) {
         return res.getString(R.string.months_ago, (int) Math.floor(deltaMinutes / (60 * 24 * 30)));
      } else if (deltaMinutes < (24 * 60 * 731)) {
         return res.getString(R.string.last_year);
      } else {
         return res.getString(R.string.years_ago, (int) Math.floor(deltaMinutes / (60 * 24 * 365.25)));
      }
   }

   public static String convertDateToReference(Context context, Date dateTarget) {
      String[] dateArray = context.getResources().getStringArray(R.array.bucket_date_items);
      String today = context.getString(R.string.today);

      if (dateTarget == null) {
         return dateArray[DateTimeUtils.SOMETIME];
      }

      Calendar calendar = Calendar.getInstance();
      DateTime dateTimeToday = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), 0, 0);
      DateTime dateTimeTarget = new DateTime(dateTarget);


      if (dateTimeToday.isAfter(dateTimeTarget)) {
         return DateTimeUtils.convertDateToString(dateTarget, DateTimeUtils.DATE_FORMAT);
      }

      int daysBetween = Days.daysBetween(dateTimeToday, dateTimeTarget).getDays();

      int yearToday = dateTimeToday.get(DateTimeFieldType.year());
      int yearTarget = dateTimeTarget.get(DateTimeFieldType.year());

      boolean thisYear = yearToday == yearTarget;

      int weekOfYearToday = dateTimeToday.get(DateTimeFieldType.weekOfWeekyear());
      int weekOfYearTarget = dateTimeTarget.get(DateTimeFieldType.weekOfWeekyear());

      int monthToday = dateTimeToday.get(DateTimeFieldType.monthOfYear());
      int monthTarget = dateTimeTarget.get(DateTimeFieldType.monthOfYear());
      int monthsBetween = monthTarget - monthToday;

      String result;

      if (daysBetween == 0) {
         result = today;
      } else if (daysBetween == 1) {
         result = dateArray[DateTimeUtils.TOMORROW];
      } else if (thisYear && weekOfYearToday == weekOfYearTarget) {
         result = dateArray[DateTimeUtils.THIS_WEEK];
      } else if (thisYear && weekOfYearTarget - weekOfYearToday == 1) {
         result = dateArray[DateTimeUtils.NEXT_WEEK];
      } else if (thisYear && monthToday == monthTarget) {
         result = dateArray[DateTimeUtils.THIS_MONTH];
      } else if (thisYear && monthsBetween == 1) {
         result = dateArray[DateTimeUtils.NEXT_MONTH];
      } else if (monthsBetween > 1 && monthsBetween <= 6) {
         result = dateArray[DateTimeUtils.IN_SIX_MONTH];
      } else if (thisYear) {
         result = dateArray[DateTimeUtils.THIS_YEAR];
      } else if (yearTarget - yearToday == 1) {
         result = dateArray[DateTimeUtils.NEXT_YEAR];
      } else {
         result = dateArray[DateTimeUtils.SOMETIME];
      }

      return result;
   }
}

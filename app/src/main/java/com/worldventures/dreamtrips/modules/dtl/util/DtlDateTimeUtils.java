package com.worldventures.dreamtrips.modules.dtl.util;

import android.content.res.Resources;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class DtlDateTimeUtils {

   private static final String TRANSACTION_DATE_FORMAT = "MM-dd-yyyy";
   public static final String THANK_YOU_SCREEN_FORMAT = "MM-dd-yyyy HH:mm:ss";

   private DtlDateTimeUtils() {
   }

   public static String concatOperationDays(Resources res, List<OperationDay> operationDays) {
      return concatOperationDays(res, operationDays, LocaleHelper.getDefaultLocale());
   }

   public static String concatOperationDays(Resources res, List<OperationDay> operationDays, Locale locale) {
      if (operationDays == null || operationDays.isEmpty()) {
         return "";
      }
      //
      List<OperationDay> days = Queryable.from(operationDays)
            .filter(OperationDay::isHaveOperationHours)
            .filter(operationDay -> operationDay.dayOfWeek() != null)
            .toList();
      //
      if (days.isEmpty()) {
         return "";
      }
      //
      if (days.size() == Calendar.DAY_OF_WEEK) {
         return res.getString(R.string.everyday);
      }
      //
      String delimiter = days.size() == 2 ? " & " : " "; // TODO need translations??
      List<String> names = Queryable.from(days).map(day -> DateTimeUtils.getDisplayWeekDay(day.dayOfWeek()
            .getDay(), Calendar.SHORT, locale)).toList();
      return android.text.TextUtils.join(delimiter, names);
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

   public static String getStringDateFromStringUTC(String utcString) {
      try {
         SimpleDateFormat utcFormatter = new SimpleDateFormat(DateTimeUtils.REVIEWS_DATE_FORMAT, LocaleHelper.getDefaultLocale());
         utcFormatter.setTimeZone(TimeZone.getTimeZone(DateTimeUtils.UTC));
         Date utcDate = utcFormatter.parse(utcString);

         SimpleDateFormat dateFormatter = new SimpleDateFormat(TRANSACTION_DATE_FORMAT, LocaleHelper.getDefaultLocale());
         dateFormatter.setTimeZone(TimeZone.getDefault());
         return dateFormatter.format(utcDate);
      } catch (ParseException parseException) {
         parseException.printStackTrace();
      }
      return "";
   }
}

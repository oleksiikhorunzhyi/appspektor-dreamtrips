package com.worldventures.core.utils;

import android.text.format.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public final class DateTimeUtils {

   private static final String FILTER_PATTERN = "dd MMM yyyy";
   public static final int TOMORROW = 1;
   public static final int THIS_WEEK = 2;
   public static final int NEXT_WEEK = 3;
   public static final int THIS_MONTH = 4;
   public static final int NEXT_MONTH = 5;
   public static final int IN_SIX_MONTH = 6;
   public static final int THIS_YEAR = 7;
   public static final int NEXT_YEAR = 8;
   public static final int SOMETIME = 9;

   public static final String DATE_FORMAT = "MMM dd, yyyy";
   public static final String TIME_FORMAT = "hh:mm a";
   public static final String MEMBER_FORMAT = "MMM dd, hha";

   public static final String FULL_SCREEN_PHOTO_DATE_FORMAT = "MMM dd, yyyy hh:mma";
   public static final String FEED_DATE_FORMAT = "MMM d, yyyy  h:mm a";
   public static final String USER_DATE_FORMAT = "MMM d, yyyy hh:mm:ss a";
   public static final String DEFAULT_ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";
   public static final String ISO_FORMAT_WITH_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
   public static final String PODCAST_DATE_FORMAT = "MMM d, yyyy";
   public static final String TRIP_FILTER_ANALYTIC_DATE_FORMAT = "MM-dd-yyyy";
   public static final String REVIEWS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
   public static final String TRANSACTION_DATE_FORMAT = "MM-dd-yyyy";
   public static final String TRANSACTION_DATE_FORMAT_FULL = "MM-dd-yyyy HH:mm";

   public static final String UTC = "UTC";

   private DateTimeUtils() {
   }

   public static String convertDateForFilters(Date date) {
      return convertDateToString(date, FILTER_PATTERN);
   }

   public static DateFormat getDefaultISOFormat() {
      return new SimpleDateFormat(DEFAULT_ISO_FORMAT, LocaleHelper.getDefaultLocale());
   }

   public static DateFormat[] getISO1DateFormats() {
      return new DateFormat[]{
            new SimpleDateFormat(ISO_FORMAT_WITH_TIMEZONE, LocaleHelper.getDefaultLocale()),
            new SimpleDateFormat(DEFAULT_ISO_FORMAT, LocaleHelper.getDefaultLocale()),
            new SimpleDateFormat(USER_DATE_FORMAT, LocaleHelper.getDefaultLocale()),
            new SimpleDateFormat("yyyy-MM-dd", LocaleHelper.getDefaultLocale()),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ", LocaleHelper.getDefaultLocale()),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", LocaleHelper.getDefaultLocale()),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm.ss.SSS'Z'", LocaleHelper.getDefaultLocale())
      };
   }

   public static String convertDateToString(Date date, DateFormat format) {
      if (date != null) {
         return format.format(date);
      } else {
         return null;
      }
   }

   public static String convertDateToString(Date date, String format) {
      SimpleDateFormat sdf = new SimpleDateFormat(format, LocaleHelper.getDefaultLocale());
      if (date != null) {
         return sdf.format(date);
      } else {
         return null;
      }
   }

   public static String convertDateToString(int year, int month, int day) {
      return convertDateToString(year, month, day, DATE_FORMAT);
   }

   public static String convertDateToString(int year, int month, int day, String dateFormat) {
      SimpleDateFormat sim = new SimpleDateFormat(dateFormat, LocaleHelper.getDefaultLocale());

      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(Calendar.MONTH, month);
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.DAY_OF_MONTH, day);
      Date date = calendar.getTime();
      return sim.format(date);
   }


   public static String convertTimeToString(int h, int m) {
      return convertTimeToString(h, m, TIME_FORMAT);
   }

   /**
    * Format seconds in float (ex. 96.8) to String (ex. 01:36)
    *
    * @param seconds in float
    * @return String mm:ss
    */
   public static String convertTimeToString(float seconds) {
      return DateUtils.formatElapsedTime((long) (seconds));
   }

   public static String convertTimeToString(int h, int m, String timeFormat) {
      SimpleDateFormat sim = new SimpleDateFormat(timeFormat, LocaleHelper.getDefaultLocale());
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(Calendar.HOUR, h);
      calendar.set(Calendar.MINUTE, m);
      Date date = calendar.getTime();
      return sim.format(date);
   }


   public static Date mergeDateTime(Date date, Date time) {
      Calendar calendarA = Calendar.getInstance();
      calendarA.setTime(date);
      Calendar calendarB = Calendar.getInstance();
      calendarB.setTime(time);

      calendarA.set(Calendar.HOUR_OF_DAY, calendarB.get(Calendar.HOUR_OF_DAY));
      calendarA.set(Calendar.MINUTE, calendarB.get(Calendar.MINUTE));
      calendarA.set(Calendar.SECOND, calendarB.get(Calendar.SECOND));
      calendarA.set(Calendar.MILLISECOND, calendarB.get(Calendar.MILLISECOND));

      return calendarA.getTime();
   }

   public static Date mergeDateTime(String date, String time) {
      return mergeDateTime(dateFromString(date), timeFromString(time));
   }

   public static String convertSecondsToString(int seconds) {
      SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", LocaleHelper.getDefaultLocale());
      return formatter.format(new Date(seconds * 1000L));
   }

   public static Date dateFromString(String date) {
      return dateFromString(date, DATE_FORMAT);
   }

   public static Date timeFromString(String time) {
      return dateFromString(time, TIME_FORMAT);
   }

   public static Date dateFromString(String date, String dateFormat) {
      if (date != null) {
         DateFormat result = new SimpleDateFormat(dateFormat, LocaleHelper.getDefaultLocale());
         try {
            return result.parse(date);
         } catch (ParseException e) {
            Timber.e(e, "");
         }
      }
      return null;
   }

   public static String currentUtcString() {
      return convertDateToUTCString(new Date(System.currentTimeMillis()));
   }

   public static String convertDateToUTCString(Date date) {
      DateTime dt = new DateTime(date);
      DateTimeFormatter fmt = DateTimeFormat.forPattern(ISO_FORMAT_WITH_TIMEZONE);
      fmt = fmt.withZone(DateTimeZone.UTC);
      return fmt.print(dt);
   }

   public static String convertDateToReference(Date dateTarget, String[] dateArray, String today) {
      if (dateTarget == null) {
         return dateArray[SOMETIME];
      }

      Calendar calendar = Calendar.getInstance();
      DateTime dateTimeToday = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), 0, 0);
      DateTime dateTimeTarget = new DateTime(dateTarget);


      if (dateTimeToday.isAfter(dateTimeTarget)) {
         return convertDateToString(dateTarget, DATE_FORMAT);
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
         result = dateArray[TOMORROW];
      } else if (thisYear && weekOfYearToday == weekOfYearTarget) {
         result = dateArray[THIS_WEEK];
      } else if (thisYear && weekOfYearTarget - weekOfYearToday == 1) {
         result = dateArray[NEXT_WEEK];
      } else if (thisYear && monthToday == monthTarget) {
         result = dateArray[THIS_MONTH];
      } else if (thisYear && monthsBetween == 1) {
         result = dateArray[NEXT_MONTH];
      } else if (monthsBetween > 1 && monthsBetween <= 6) {
         result = dateArray[IN_SIX_MONTH];
      } else if (thisYear) {
         result = dateArray[THIS_YEAR];
      } else if (yearTarget - yearToday == 1) {
         result = dateArray[NEXT_YEAR];
      } else {
         result = dateArray[SOMETIME];
      }

      return result;
   }

   public static Date convertReferenceToDate(int position) {
      Calendar calendar = Calendar.getInstance();

      switch (position) {
         case TOMORROW:
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
            break;
         case THIS_WEEK:
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
            break;
         case NEXT_WEEK:
            calendar.add(Calendar.WEEK_OF_MONTH, 1);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
            break;
         case THIS_MONTH:
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            break;
         case NEXT_MONTH:
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            break;
         case IN_SIX_MONTH:
            calendar.add(Calendar.MONTH, 6);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            break;
         case THIS_YEAR:
            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            break;
         case NEXT_YEAR:
            calendar.add(Calendar.YEAR, 1);
            calendar.add(Calendar.MONTH, 1);
            break;
         default:
            break;
      }

      return calendar.getTime();
   }

   public static boolean isSameDayOfWeek(int calendarDayOfWeek, String dayName) {
      String weekDay = getDisplayWeekDay(calendarDayOfWeek, Calendar.SHORT).toLowerCase();
      String day = dayName.toLowerCase();
      return day.startsWith(weekDay);
   }

   public static String getDisplayWeekDay(int dayOfWeek, int style) {
      return getDisplayWeekDay(dayOfWeek, style, Locale.ENGLISH);
   }

   public static String getDisplayWeekDay(int dayOfWeek, int style, Locale locale) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
      return calendar.getDisplayName(Calendar.DAY_OF_WEEK, style, locale);
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

   public static String getISODateFromStringUTC(String utcString) {
      try {
         SimpleDateFormat utcFormatter = new SimpleDateFormat(DateTimeUtils.REVIEWS_DATE_FORMAT, LocaleHelper.getDefaultLocale());
         utcFormatter.setTimeZone(TimeZone.getTimeZone(DateTimeUtils.UTC));
         Date utcDate = utcFormatter.parse(utcString);

         SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_ISO_FORMAT, LocaleHelper.getDefaultLocale());
         dateFormatter.setTimeZone(TimeZone.getDefault());
         return dateFormatter.format(utcDate);
      } catch (ParseException parseException) {
         parseException.printStackTrace();
      }
      return "";
   }
}


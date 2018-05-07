package com.worldventures.dreamtrips.modules.trips.model;

import com.worldventures.core.utils.LocaleHelper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Schedule implements Serializable {

   private static final String PATTERN_YEAR_MONTH_AND_DAY = "MMM d, yyyy";
   private static final String PATTERN_MONTH_AND_DAY = "MMM d";
   private static final String PATTERN_DAY = "d";
   private static final String PATTERN_YEAR_AND_DAY = "d, yyyy";
   private static final String TIMEZONE_UTC = "UTC";

   private static final SimpleDateFormat SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY = new SimpleDateFormat(PATTERN_YEAR_MONTH_AND_DAY,
         LocaleHelper.getDefaultLocale());
   private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MONTH_DAY = new SimpleDateFormat(PATTERN_MONTH_AND_DAY, LocaleHelper
         .getDefaultLocale());
   private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(PATTERN_DAY, LocaleHelper.getDefaultLocale());
   private static final SimpleDateFormat SIMPLE_DATE_FORMAT_YEAR_DAY = new SimpleDateFormat(PATTERN_YEAR_AND_DAY, LocaleHelper
         .getDefaultLocale());

   static {
      SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
      SIMPLE_DATE_FORMAT_MONTH_DAY.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
      SIMPLE_DATE_FORMAT_DAY.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
      SIMPLE_DATE_FORMAT_YEAR_DAY.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
   }

   private Date startDate;
   private Date endDate;


   public Schedule(Date startDate, Date endDate) {
      this.startDate = startDate;
      this.endDate = endDate;
   }

   public Schedule() {
      // This constructor is intentionally empty. Nothing special is needed here.
   }

   public synchronized String getStartDateString() {
      return SIMPLE_DATE_FORMAT_MONTH_DAY.format(startDate);
   }

   public Date getStartDate() {
      return startDate;
   }

   public void setStartDate(Date startDate) {
      this.startDate = startDate;
   }

   public Date getEndDate() {
      return endDate;
   }

   public void setEndDate(Date endDate) {
      this.endDate = endDate;
   }

   @Override
   public synchronized String toString() {
      Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_UTC));
      calendarStart.setTimeInMillis(startDate.getTime());
      Calendar calendarEnd = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_UTC));
      calendarEnd.setTimeInMillis(endDate.getTime());

      String startDateString;

      if (calendarStart.get(Calendar.YEAR) != calendarEnd.get(Calendar.YEAR)) {
         startDateString = SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY.format(startDate);
      } else {
         startDateString = SIMPLE_DATE_FORMAT_MONTH_DAY.format(startDate);
      }

      String endDateString;
      if (calendarEnd.get(Calendar.MONTH) != calendarStart.get(Calendar.MONTH)) {
         endDateString = SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY.format(endDate);
      } else {
         endDateString = SIMPLE_DATE_FORMAT_YEAR_DAY.format(endDate);
      }

      return startDateString + " - " + endDateString;
   }
}

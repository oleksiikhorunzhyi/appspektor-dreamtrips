package com.worldventures.dreamtrips.modules.trips.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Schedule implements Serializable {

   private static final String PATTERN_YEAR_MONTH_AND_DAY = "MMM d, yyyy";
   private static final String PATTERN_MONTH_AND_DAY = "MMM d";
   private static final String PATTERN_DAY = "d";
   private static final String PATTERN_YEAR_AND_DAY = "d, yyyy";

   private final static SimpleDateFormat simpleDateFormatYearMonthDay;
   private final static SimpleDateFormat simpleDateFormatMonthDay;
   private final static SimpleDateFormat simpleDateFormatDay;
   private final static SimpleDateFormat simpleDateFormatYearDay;

   static {
      simpleDateFormatYearMonthDay = new SimpleDateFormat(PATTERN_YEAR_MONTH_AND_DAY, Locale.getDefault());
      simpleDateFormatYearMonthDay.setTimeZone(TimeZone.getTimeZone("UTC"));
      simpleDateFormatMonthDay = new SimpleDateFormat(PATTERN_MONTH_AND_DAY, Locale.getDefault());
      simpleDateFormatMonthDay.setTimeZone(TimeZone.getTimeZone("UTC"));
      simpleDateFormatDay = new SimpleDateFormat(PATTERN_DAY, Locale.getDefault());
      simpleDateFormatDay.setTimeZone(TimeZone.getTimeZone("UTC"));
      simpleDateFormatYearDay = new SimpleDateFormat(PATTERN_YEAR_AND_DAY, Locale.getDefault());
      simpleDateFormatYearDay.setTimeZone(TimeZone.getTimeZone("UTC"));
   }


   @SerializedName("start_on") private Date startOn;
   @SerializedName("end_on") private Date endOn;

   public Schedule() {
   }

   public java.util.Date getStartDate() {
      return startOn;
   }

   public void setStartDate(java.util.Date startOn) {
      this.startOn = startOn;
   }

   public java.util.Date getEndDate() {
      return endOn;
   }

   public void setEndDate(java.util.Date endDate) {
      this.endOn = endDate;
   }

   public boolean check(DateFilterItem dateFilterItem) {
      return (startOn.equals(dateFilterItem.getStartDate()) || startOn.after(dateFilterItem.getStartDate())) && (endOn.equals(dateFilterItem
            .getEndDate()) || endOn.before(dateFilterItem.getEndDate()));
   }

   public String getStartDateString() {
      return simpleDateFormatMonthDay.format(getStartDate());
   }

   @Override
   public String toString() {
      Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      calendarStart.setTimeInMillis(startOn.getTime());
      Calendar calendarEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      calendarEnd.setTimeInMillis(endOn.getTime());

      return new StringBuilder().append(calendarStart.get(Calendar.YEAR) != calendarEnd.get(Calendar.YEAR) ? simpleDateFormatYearMonthDay
            .format(getStartDate()) : simpleDateFormatMonthDay.format(getStartDate()))
            .append(" - ")
            .append(calendarEnd.get(Calendar.MONTH) != calendarStart.get(Calendar.MONTH) ? simpleDateFormatYearMonthDay.format(getEndDate()) : simpleDateFormatYearDay
                  .format(getEndDate()))
            .toString();
   }
}

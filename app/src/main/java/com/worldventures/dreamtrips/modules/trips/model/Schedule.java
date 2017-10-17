package com.worldventures.dreamtrips.modules.trips.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.trips.model.filter.DateFilterItem;

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

   private final static SimpleDateFormat SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY;
   private final static SimpleDateFormat SIMPLE_DATE_FORMAT_MONTH_DAY;
   private final static SimpleDateFormat SIMPLE_DATE_FORMAT_DAY;
   private final static SimpleDateFormat SIMPLE_DATE_FORMAT_YEAR_DAY;

   static {
      SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY = new SimpleDateFormat(PATTERN_YEAR_MONTH_AND_DAY, LocaleHelper.getDefaultLocale());
      SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
      SIMPLE_DATE_FORMAT_MONTH_DAY = new SimpleDateFormat(PATTERN_MONTH_AND_DAY, LocaleHelper.getDefaultLocale());
      SIMPLE_DATE_FORMAT_MONTH_DAY.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
      SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(PATTERN_DAY, LocaleHelper.getDefaultLocale());
      SIMPLE_DATE_FORMAT_DAY.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
      SIMPLE_DATE_FORMAT_YEAR_DAY = new SimpleDateFormat(PATTERN_YEAR_AND_DAY, LocaleHelper.getDefaultLocale());
      SIMPLE_DATE_FORMAT_YEAR_DAY.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
   }


   @SerializedName("start_on") private Date startOn;
   @SerializedName("end_on") private Date endOn;

   public Date getStartDate() {
      return startOn;
   }

   public void setStartDate(Date startOn) {
      this.startOn = startOn;
   }

   public Date getEndDate() {
      return endOn;
   }

   public void setEndDate(Date endDate) {
      this.endOn = endDate;
   }

   public boolean check(DateFilterItem dateFilterItem) {
      return (startOn.equals(dateFilterItem.getStartDate()) || startOn.after(dateFilterItem.getStartDate())) && (endOn.equals(dateFilterItem
            .getEndDate()) || endOn.before(dateFilterItem.getEndDate()));
   }

   public synchronized String getStartDateString() {
      return SIMPLE_DATE_FORMAT_MONTH_DAY.format(getStartDate());
   }

   @Override
   public synchronized String toString() {
      Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_UTC));
      calendarStart.setTimeInMillis(startOn.getTime());
      Calendar calendarEnd = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_UTC));
      calendarEnd.setTimeInMillis(endOn.getTime());

      return new StringBuilder().append(calendarStart.get(Calendar.YEAR) != calendarEnd.get(Calendar.YEAR) ? SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY
            .format(getStartDate()) : SIMPLE_DATE_FORMAT_MONTH_DAY.format(getStartDate()))
            .append(" - ")
            .append(calendarEnd.get(Calendar.MONTH) != calendarStart.get(Calendar.MONTH) ? SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY
                  .format(getEndDate()) : SIMPLE_DATE_FORMAT_YEAR_DAY
                  .format(getEndDate()))
            .toString();
   }
}

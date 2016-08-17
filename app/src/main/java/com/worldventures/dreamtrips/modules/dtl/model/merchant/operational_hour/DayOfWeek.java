package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.util.Calendar;

public enum DayOfWeek {
   MONDAY(Calendar.MONDAY),
   TUESDAY(Calendar.TUESDAY),
   WEDNESDAY(Calendar.WEDNESDAY),
   THURSDAY(Calendar.THURSDAY),
   FRIDAY(Calendar.FRIDAY),
   SATURDAY(Calendar.SATURDAY),
   SUNDAY(Calendar.SUNDAY);

   private int calendarDayOfWeek;

   DayOfWeek(int calendarDayOfWeek) {
      this.calendarDayOfWeek = calendarDayOfWeek;
   }

   public int getDay() {
      return calendarDayOfWeek;
   }

   public static DayOfWeek from(int calendarDayOfWeek) {
      return Queryable.from(values()).first(element -> element.calendarDayOfWeek == calendarDayOfWeek);
   }

   public static DayOfWeek from(String calendarDayOfWeek) {
      return Queryable.from(values())
            .firstOrDefault(element -> DateTimeUtils.isSameDayOfWeek(element.calendarDayOfWeek, calendarDayOfWeek));
   }
}

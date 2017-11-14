package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.utils.LocaleHelper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.worldventures.core.utils.DateTimeUtils.getDisplayWeekDay;

public final class OperationalHoursUtils {

   private OperationalHoursUtils() {
   }

   public static String concatOperationDays(List<OperationDay> operationDays, String everyday) {
      return concatOperationDays(operationDays, LocaleHelper.getDefaultLocale(), everyday);
   }

   public static String concatOperationDays(List<OperationDay> operationDays, Locale locale, String everyday) {
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
         return everyday;
      }
      //
      String delimiter = days.size() == 2 ? " & " : " "; // TODO need translations??
      List<String> names = Queryable.from(days).map(day -> getDisplayWeekDay(day.dayOfWeek()
            .getDay(), Calendar.SHORT, locale)).toList();
      return android.text.TextUtils.join(delimiter, names);
   }

   public static boolean isSameDayOfWeek(DayOfWeek day, int timezoneOffset) {
      DateTimeZone timeZone = DateTimeZone.forOffsetHours(timezoneOffset);
      Calendar calendar = DateTime.now(timeZone).toCalendar(Locale.US);
      return calendar.get(Calendar.DAY_OF_WEEK) == day.getDay();

   }
}

package com.worldventures.dreamtrips.modules.trips.model

import com.worldventures.core.utils.LocaleHelper
import com.worldventures.dreamtrips.modules.trips.model.filter.DateFilterItem
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class Schedule(val startDate: Date = Date(), val endDate: Date = Date()) : Serializable {

   val startDateString: String
      get() = SIMPLE_DATE_FORMAT_MONTH_DAY.format(startDate)

   fun check(dateFilterItem: DateFilterItem) =
         (startDate == dateFilterItem.startDate || startDate.after(dateFilterItem.startDate)) && (endDate == dateFilterItem
            .endDate || endDate.before(dateFilterItem.endDate))

   override fun toString(): String {
      val calendarStart = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_UTC)).apply { timeInMillis = startDate.time }
      val calendarEnd = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_UTC)).apply { timeInMillis = endDate.time }

      val startDate = if (calendarStart.get(Calendar.YEAR) != calendarEnd.get(Calendar.YEAR))
         SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY.format(startDate)
      else SIMPLE_DATE_FORMAT_MONTH_DAY.format(startDate)
      val endDate = if (calendarEnd.get(Calendar.MONTH) != calendarStart.get(Calendar.MONTH))
         SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY.format(endDate)
      else SIMPLE_DATE_FORMAT_YEAR_DAY.format(endDate)

      return "$startDate - $endDate"
   }

   companion object {

      private val PATTERN_YEAR_MONTH_AND_DAY = "MMM d, yyyy"
      private val PATTERN_MONTH_AND_DAY = "MMM d"
      private val PATTERN_DAY = "d"
      private val PATTERN_YEAR_AND_DAY = "d, yyyy"
      private val TIMEZONE_UTC = "UTC"

      private val SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY: SimpleDateFormat
      private val SIMPLE_DATE_FORMAT_MONTH_DAY: SimpleDateFormat
      private val SIMPLE_DATE_FORMAT_DAY: SimpleDateFormat
      private val SIMPLE_DATE_FORMAT_YEAR_DAY: SimpleDateFormat

      init {
         SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY = SimpleDateFormat(PATTERN_YEAR_MONTH_AND_DAY, LocaleHelper.getDefaultLocale())
         SIMPLE_DATE_FORMAT_YEAR_MONTH_DAY.timeZone = TimeZone.getTimeZone(TIMEZONE_UTC)
         SIMPLE_DATE_FORMAT_MONTH_DAY = SimpleDateFormat(PATTERN_MONTH_AND_DAY, LocaleHelper.getDefaultLocale())
         SIMPLE_DATE_FORMAT_MONTH_DAY.timeZone = TimeZone.getTimeZone(TIMEZONE_UTC)
         SIMPLE_DATE_FORMAT_DAY = SimpleDateFormat(PATTERN_DAY, LocaleHelper.getDefaultLocale())
         SIMPLE_DATE_FORMAT_DAY.timeZone = TimeZone.getTimeZone(TIMEZONE_UTC)
         SIMPLE_DATE_FORMAT_YEAR_DAY = SimpleDateFormat(PATTERN_YEAR_AND_DAY, LocaleHelper.getDefaultLocale())
         SIMPLE_DATE_FORMAT_YEAR_DAY.timeZone = TimeZone.getTimeZone(TIMEZONE_UTC)
      }
   }
}

package com.worldventures.dreamtrips.social.media_picker

import com.worldventures.dreamtrips.modules.feed.view.cell.util.PickerVideoDurationFormatter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.joda.time.MutablePeriod
import kotlin.test.assertEquals

class DurationFormatterSpec : Spek({
   describe("Duration formatter") {
      it ("should correctly process 0 duration") {
         assertEquals("0:00", format(seconds = 0))
      }

      it ("should  correctly format duration") {
         assertEquals("0:22", format(seconds = 22))
         assertEquals("11:22", format(minutes = 11, seconds = 22))
         assertEquals("12:11:22", format(hours = 12, minutes = 11, seconds = 22))
      }

      it ("should correctly do padding, first section should be ignored") {
         assertEquals("0:05", format(seconds = 5))
         assertEquals("1:05", format(minutes = 1, seconds = 5))
         assertEquals("1:02:05", format(hours = 1, minutes = 2, seconds = 5))
      }
   }

}) {
   companion object  {
      fun format(seconds: Int): String {
         return format(0, seconds)
      }

      fun format(minutes: Int, seconds: Int): String {
         return format(0, minutes, seconds)
      }

      fun format(hours: Int, minutes: Int, seconds: Int): String {
         val millis = MutablePeriod(hours, minutes, seconds, 0).toPeriod().toStandardDuration().millis
         return PickerVideoDurationFormatter.getFormattedDuration(millis)
      }
   }
}

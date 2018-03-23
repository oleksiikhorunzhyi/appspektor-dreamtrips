package com.worldventures.dreamtrips.modules.dtl.helper.howtopayvideo

import com.worldventures.dreamtrips.modules.dtl.domain.storage.snappy.DtlSnappyRepository
import java.util.concurrent.TimeUnit

class HowToPayHintDelegate(private val snappyRepository: DtlSnappyRepository) {

   private var initialized = false
   private var cachedShouldShow = true
   private var cachedTimestamp = 0L

   fun shouldShowHowToPayHint(): Boolean {
      if (!initialized) {
         cachedShouldShow = snappyRepository.shouldShowHowToVideoHint()
         cachedTimestamp = snappyRepository.lastRemindMeLaterTimestamp()
         initialized = true
      }

      val skipTillNextDay = cachedTimestamp > 0L &&
            TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - cachedTimestamp) < 1

      return cachedShouldShow && !skipTillNextDay
   }

   fun remindLaterPressed() {
      cachedTimestamp = System.currentTimeMillis()
      snappyRepository.setRemindMeLaterTimestamp(cachedTimestamp)
   }

   fun neverShowHintAgain() {
      cachedShouldShow = false
      snappyRepository.setShouldShowHowToVideoHint(cachedShouldShow)
   }

   fun reset() {
      initialized = false
   }
}

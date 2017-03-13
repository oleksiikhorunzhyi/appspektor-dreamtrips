package com.worldventures.dreamtrips.common.janet.service

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent
import io.techery.janet.ActionHolder
import io.techery.janet.ActionService

class MockAnalyticsService : ActionService() {
   override fun <A : Any?> sendInternal(holder: ActionHolder<A>?) {
   }

   override fun <A : Any?> cancel(holder: ActionHolder<A>?) {
   }

   override fun getSupportedAnnotationType(): Class<*> {
      return AnalyticsEvent::class.java
   }
}
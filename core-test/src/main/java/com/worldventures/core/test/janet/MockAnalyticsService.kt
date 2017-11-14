package com.worldventures.core.test.janet

import com.worldventures.janet.analytics.AnalyticsEvent
import io.techery.janet.ActionHolder
import io.techery.janet.ActionService

class MockAnalyticsService : ActionService() {
   override fun <A : Any?> sendInternal(holder: ActionHolder<A>?) {
   }

   override fun <A : Any?> cancel(holder: ActionHolder<A>?) {
   }

   override fun getSupportedAnnotationType() = AnalyticsEvent::class.java
}
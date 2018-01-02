package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.core.service.analytics.BaseAnalyticsAction

import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
abstract class SendProgressAnalyticsIfNeed<E : BaseAnalyticsAction>(private val expectedAnalyticStep: Int,
                                                                    private val currentProgress: Long,
                                                                    private val totalLength: Long) : Command<Int>() {

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Int>) {
      val percent = (currentProgress * 100 / totalLength).toInt()
      val currentStep = percent / STEP_PERCENT
      val action = chooseAnalyticAction(currentStep, expectedAnalyticStep)
      if (action != null) {
         sendAnalyticAction(action)
         callback.onSuccess(expectedAnalyticStep + 1)
      } else {
         callback.onSuccess(expectedAnalyticStep)
      }
   }

   protected abstract fun chooseAnalyticAction(currentStep: Int, expectedAnalyticStep: Int): E?

   protected abstract fun sendAnalyticAction(action: E)

   companion object {
      const val STEP_PERCENT = 25
   }
}

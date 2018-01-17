package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.core.service.analytics.AnalyticsInteractor
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.WatchVideoAnalyticAction
import com.worldventures.dreamtrips.social.ui.reptools.presenter.TrainingVideosPresenter
import com.worldventures.dreamtrips.social.ui.video.presenter.HelpVideosPresenter
import com.worldventures.dreamtrips.social.ui.video.presenter.PresentationVideosPresenter
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class SendVideoAnalyticsIfNeedAction(private val launchComponent: Class<*>, private val language: String,
                                     private val videoName: String, expectedAnalyticStep: Int, currentVideoProgress: Long, totalVideoLength: Long) :
         SendProgressAnalyticsIfNeed<WatchVideoAnalyticAction>(expectedAnalyticStep, currentVideoProgress, totalVideoLength), InjectableAction {

   @Inject internal lateinit var analyticsInteractor: AnalyticsInteractor

   override fun chooseAnalyticAction(currentStep: Int, expectedAnalyticStep: Int): WatchVideoAnalyticAction? {
      if (expectedAnalyticStep == 0) {
         return WatchVideoAnalyticAction.startVideo(language, videoName, chooseAnalyticNamespace())
      }

      if (currentStep < expectedAnalyticStep) {
         return null
      }

      var action: WatchVideoAnalyticAction? = null
      when (currentStep) {
         STEP_1 -> action = WatchVideoAnalyticAction.progress25(language, videoName, chooseAnalyticNamespace())
         STEP_2 -> action = WatchVideoAnalyticAction.progress50(language, videoName, chooseAnalyticNamespace())
         STEP_3 -> action = WatchVideoAnalyticAction.progress75(language, videoName, chooseAnalyticNamespace())
         STEP_4 -> action = WatchVideoAnalyticAction.progress100(language, videoName, chooseAnalyticNamespace())
         else -> {
         }
      }
      return action
   }

   private fun chooseAnalyticNamespace(): String? {
      if (launchComponent == HelpVideosPresenter::class.java) {
         return WatchVideoAnalyticAction.HELP_VIDEO_NAMESPASE
      }
      if (launchComponent == PresentationVideosPresenter::class.java) {
         return WatchVideoAnalyticAction.MEMBERSHIP_VIDEOS_NAMESPASE
      }
      return if (launchComponent == TrainingVideosPresenter::class.java) {
         WatchVideoAnalyticAction.REPTOOLS_TRAINING_VIDEOS_NAMESPASE
      } else null
   }

   override fun sendAnalyticAction(action: WatchVideoAnalyticAction) =
         analyticsInteractor.analyticsActionPipe().send(action)

   companion object {
      const val STEP_1 = 1
      const val STEP_2 = 2
      const val STEP_3 = 3
      const val STEP_4 = 4
   }
}

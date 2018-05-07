package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.core.service.analytics.AnalyticsInteractor
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.WatchVideoAnalyticAction
import com.worldventures.dreamtrips.social.ui.reptools.presenter.TrainingVideosPresenter
import com.worldventures.dreamtrips.social.ui.video.presenter.HelpVideosPresenter
import com.worldventures.dreamtrips.social.ui.video.presenter.PresentationVideosPresenter
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class SendAnalyticsIfNeedAction(private val launchComponent: Class<*>, private val language: String,
                                private val videoName: String, private val expectedAnalyticStep: Int,
                                private val currentVideoProgress: Long, private val totalVideoLength: Long) : Command<Int>(), InjectableAction {

   @Inject internal lateinit var analyticsInteractor: AnalyticsInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Int>) {
      val percent = (currentVideoProgress * 100 / totalVideoLength).toInt()
      val step = percent / PERCENT_STEP
      val action = chooseAnalyticAction(step, expectedAnalyticStep)
      if (action != null) {
         analyticsInteractor.analyticsActionPipe().send(action)
         callback.onSuccess(expectedAnalyticStep + 1)
      } else {
         callback.onSuccess(expectedAnalyticStep)
      }
   }

   private fun chooseAnalyticAction(analyticStep: Int, currentAnalyticStep: Int): WatchVideoAnalyticAction? {
      if (currentAnalyticStep == 0) {
         return WatchVideoAnalyticAction.startVideo(language, videoName, chooseAnalyticsNamespace())
      }

      if (analyticStep < currentAnalyticStep) {
         return null
      }

      var action: WatchVideoAnalyticAction? = null
      when (analyticStep) {
         PROGRESS_25_STEP -> action = WatchVideoAnalyticAction.progress25(language, videoName, chooseAnalyticsNamespace())
         PROGRESS_50_STEP -> action = WatchVideoAnalyticAction.progress50(language, videoName, chooseAnalyticsNamespace())
         PROGRESS_75_STEP -> action = WatchVideoAnalyticAction.progress75(language, videoName, chooseAnalyticsNamespace())
         PROGRESS_100_STEP -> action = WatchVideoAnalyticAction.progress100(language, videoName, chooseAnalyticsNamespace())
         else -> {
         }
      }
      return action
   }

   private fun chooseAnalyticsNamespace(): String? {
      return when (launchComponent) {
         HelpVideosPresenter::class.java -> WatchVideoAnalyticAction.HELP_VIDEO_NAMESPASE
         PresentationVideosPresenter::class.java -> WatchVideoAnalyticAction.MEMBERSHIP_VIDEOS_NAMESPASE
         TrainingVideosPresenter::class.java -> WatchVideoAnalyticAction.REPTOOLS_TRAINING_VIDEOS_NAMESPASE
         else -> null
      }
   }

   companion object {
      const val PERCENT_STEP = 25

      const val PROGRESS_25_STEP = 1
      const val PROGRESS_50_STEP = 2
      const val PROGRESS_75_STEP = 3
      const val PROGRESS_100_STEP = 4
   }
}

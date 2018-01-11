package com.worldventures.dreamtrips.social.ui.reptools.presenter

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.modules.video.service.MemberVideosInteractor
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.LoadCanceledAction
import com.worldventures.dreamtrips.social.ui.reptools.delegate.LocaleVideoDelegate
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.AdobeTrainingVideosViewedAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ReptoolsVideoDownloadedAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ReptoolsVideoViewedAction
import com.worldventures.dreamtrips.social.ui.video.presenter.VideoBasePresenter
import com.worldventures.dreamtrips.social.ui.video.service.VideoHelperInteractor
import com.worldventures.dreamtrips.social.ui.video.service.command.DetermineHeadersCommand
import com.worldventures.dreamtrips.social.ui.video.service.command.HeaderType
import io.techery.janet.helper.ActionStateSubscriber
import timber.log.Timber
import javax.inject.Inject

open class TrainingVideosPresenter : VideoBasePresenter<TrainingVideosPresenter.View>() {

   @Inject lateinit var localVideoDelegate: LocaleVideoDelegate
   @Inject lateinit var headerInteractor: VideoHelperInteractor
   @Inject lateinit var memberVideosInteractor: MemberVideosInteractor

   override fun takeView(view: View) {
      super.takeView(view)
      subscribeToLocalsPipe()
   }

   override fun onResume() {
      super.onResume()
      loadLocals()
   }

   override fun reload() {
      loadLocals()
   }

   open fun subscribeToLocalsPipe() {
      memberVideosInteractor.videoLocalesPipe
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetVideoLocalesCommand>()
                  .onStart { view.startLoading() }
                  .onSuccess {
                     localVideoDelegate.saveVideoLocaleList(it.result)
                     processLocales()
                  }
                  .onFail { command, exception ->
                     handleError(command, exception)
                     memberVideosInteractor.videoLocalesPipe.clearReplays()
                  })
   }

   fun loadVideos(videoLanguage: VideoLanguage) {
      memberVideosInteractor.memberVideosPipe.
            createObservableResult(GetMemberVideosCommand.forRepVideos(videoLanguage))
            .flatMap {
               headerInteractor.headerPipe.createObservableResult(DetermineHeadersCommand(HeaderType.TRAINING, it.result))
            }.compose(bindViewToMainComposer())
            .subscribe({
               view.finishLoading()
               view.setItems(it.result)
            }, {
               view.finishLoading()
               Timber.e(it)
            })
   }

   private fun processLocales() {
      val videoLocales = localVideoDelegate.fetchVideoLocaleList()
      val videoData = localVideoDelegate.fetchLocaleAndLanguage(context, videoLocales)
      view.setLocales(ArrayList(videoLocales), videoData.first)
      loadVideos(videoData.second)
   }

   fun onLanguageSelected(videoLocale: VideoLocale, videoLanguage: VideoLanguage) {
      localVideoDelegate.saveVideoLocaleAndLanguage(videoLocale, videoLanguage)
      processLocales()
   }

   private fun loadLocals() = memberVideosInteractor.videoLocalesPipe.send(GetVideoLocalesCommand())

   fun trackView() = analyticsInteractor.analyticsActionPipe().send(AdobeTrainingVideosViewedAction())

   override fun onPlayVideo(video: Video) {
      super.onPlayVideo(video)
      analyticsInteractor.analyticsActionPipe().send(ReptoolsVideoViewedAction(video.videoName))
   }

   override fun downloadVideoAccepted(video: Video) {
      super.downloadVideoAccepted(video)
      analyticsInteractor.analyticsActionPipe().send(ReptoolsVideoDownloadedAction(video.videoName))
   }

   override fun cancelCachingAccepted(entity: CachedModel) {
      super.cancelCachingAccepted(entity)
      analyticsInteractor.analyticsActionPipe().send(LoadCanceledAction())
   }

   interface View : VideoBasePresenter.View {
      fun setLocales(locales: ArrayList<VideoLocale>, defaultValue: VideoLocale?)

      fun showDialog()

      fun setItems(videos: List<Any>)
   }
}

package com.worldventures.dreamtrips.social.ui.video.presenter

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.model.VideoCategory
import com.worldventures.core.modules.video.service.MemberVideosInteractor
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.LoadCanceledAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ReptoolsVideoViewedAction
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageVideo360DownloadedAction
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageVideo360StartedDownloadingAction
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageVideo360StartedPlaying
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageVideo360ViewedAction
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImagesTabViewAnalyticsEvent
import com.worldventures.dreamtrips.social.ui.video.service.VideoHelperInteractor
import com.worldventures.dreamtrips.social.ui.video.service.command.DetermineHeadersCommand
import com.worldventures.dreamtrips.social.ui.video.service.command.HeaderType
import com.worldventures.dreamtrips.social.ui.video.service.command.SortVideo360CategoriesCommand
import io.techery.janet.helper.ActionStateSubscriber

import javax.inject.Inject

open class ThreeSixtyVideosPresenter : VideoBasePresenter<ThreeSixtyVideosPresenter.View>() {

   @Inject lateinit var headerInteractor: VideoHelperInteractor
   @Inject lateinit var memberVideosInteractor: MemberVideosInteractor

   override fun takeView(view: View) {
      super.takeView(view)
      subscribeToVideosPipe()
   }

   override fun onResume() {
      super.onResume()
      loadVideos()
   }

   override fun reload() {
      loadVideos()
   }

   open fun subscribeToVideosPipe() {
      memberVideosInteractor.memberVideosPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetMemberVideosCommand>()
                  .onStart { view.startLoading() }
                  .onSuccess { fetchHeaders(it.result) }
                  .onFail { command, exception ->
                     view.finishLoading()
                     handleError(command, exception)
                  })
   }

   open fun fetchHeaders(categories: List<VideoCategory>) {
      headerInteractor.headerPipe
            .createObservableResult(DetermineHeadersCommand(HeaderType.TREESIXTY, categories))
            .flatMap { headerInteractor.sort360VideoPipe.createObservableResult(SortVideo360CategoriesCommand(it.result)) }
            .map { it.result }
            .compose(bindViewToMainComposer())
            .subscribe {
               view.finishLoading()
               view.setItems(obtainArrayList(it.all), obtainArrayList(it.featured), obtainArrayList(it.recent))
            }
   }

   private fun obtainArrayList(items: List<Any>?) = if (items != null) ArrayList(items) else null

   fun loadVideos() = memberVideosInteractor.memberVideosPipe.send(GetMemberVideosCommand.forThreeSixtyVideos())

   fun onSelected() {
      analyticsInteractor.analyticsActionPipe().send(TripImagesTabViewAnalyticsEvent.for360Video())
   }

   fun openVideo360Required(video: Video) {
      var url = video.videoUrl
      if (cachedModelHelper.isCached(video.cacheEntity)) {
         url = cachedModelHelper.getFilePath(url)
      }
      activityRouter.open360Activity(url, video.videoName)

      analyticsInteractor.analyticsActionPipe().send(TripImageVideo360StartedPlaying())
      analyticsInteractor.analyticsActionPipe().send(TripImageVideo360ViewedAction(video.videoName))
   }

   override fun onPlayVideo(video: Video) {
      super.onPlayVideo(video)
      analyticsInteractor.analyticsActionPipe().send(ReptoolsVideoViewedAction(video.videoName))
   }

   override fun downloadVideoAccepted(video: Video) {
      super.downloadVideoAccepted(video)

      analyticsInteractor.analyticsActionPipe().send(TripImageVideo360StartedDownloadingAction())
      analyticsInteractor.analyticsActionPipe().send(TripImageVideo360DownloadedAction(video.videoName))
   }

   override fun cancelCachingAccepted(entity: CachedModel) {
      super.cancelCachingAccepted(entity)
      analyticsInteractor.analyticsActionPipe().send(LoadCanceledAction())
   }

   interface View : VideoBasePresenter.View {
      fun setItems(allVideos: ArrayList<Any>?, featuredVideos: ArrayList<Any>?, recentVideos: ArrayList<Any>?)
   }
}

package com.worldventures.dreamtrips.social.ui.video.presenter

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.model.VideoCategory
import com.worldventures.core.modules.video.service.MemberVideosInteractor
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.LoadCanceledAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.MemberVideosViewedAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.MembershipVideoDownloadedAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.MembershipVideoStartedDownloadingAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.MembershipVideoStartedPlayingAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.MembershipVideoViewedAction
import com.worldventures.dreamtrips.social.ui.video.service.VideoHelperInteractor
import com.worldventures.dreamtrips.social.ui.video.service.command.DetermineHeadersCommand
import com.worldventures.dreamtrips.social.ui.video.service.command.HeaderType
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

open class PresentationVideosPresenter : VideoBasePresenter<PresentationVideosPresenter.View>() {

   @Inject lateinit var headerInteractor: VideoHelperInteractor
   @Inject lateinit var memberVideosInteractor: MemberVideosInteractor

   override fun takeView(view: PresentationVideosPresenter.View) {
      super.takeView(view)
      subscribeToVideosPipe()
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
            .compose(bindViewToMainComposer())
            .subscribe {
               view.finishLoading()
               view.setItems(it.result)
            }
   }

   override fun onResume() {
      super.onResume()
      loadVideos()
   }

   override fun reload() = loadVideos()

   private fun loadVideos() = memberVideosInteractor.memberVideosPipe.send(GetMemberVideosCommand.forThreeSixtyVideos())

   override fun downloadVideoAccepted(video: Video) {
      super.downloadVideoAccepted(video)
      analyticsInteractor.analyticsActionPipe().send(MembershipVideoStartedDownloadingAction())
      analyticsInteractor.analyticsActionPipe().send(MembershipVideoDownloadedAction(video.videoName))
   }

   override fun cancelCachingAccepted(entity: CachedModel) {
      super.cancelCachingAccepted(entity)
      analyticsInteractor.analyticsActionPipe().send(LoadCanceledAction())
   }

   override fun onPlayVideo(video: Video) {
      super.onPlayVideo(video)

      analyticsInteractor.analyticsActionPipe().send(MembershipVideoStartedPlayingAction())
      analyticsInteractor.analyticsActionPipe().send(MembershipVideoViewedAction(video.videoName))
   }

   fun trackView() = analyticsInteractor.analyticsActionPipe().send(MemberVideosViewedAction(accountUserId))

   interface View : VideoBasePresenter.View {
      fun setItems(videos: List<Any>)
   }
}

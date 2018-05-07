package com.worldventures.dreamtrips.social.ui.tripsimages.presenter

import android.support.v4.app.FragmentManager
import com.worldventures.dreamtrips.core.navigation.router.Router
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.feed.bundle.CommentsBundle
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityHolder
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetVideoCommand
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.CommentableFragment
import com.worldventures.dreamtrips.social.ui.flags.model.FlagData
import com.worldventures.dreamtrips.social.ui.flags.service.FlagDelegate
import com.worldventures.dreamtrips.social.ui.flags.service.FlagsInteractor
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand
import io.techery.janet.helper.ActionStateSubscriber
import rx.functions.Action2
import javax.inject.Inject

class FullscreenVideoPresenter(private var video: Video) : Presenter<FullscreenVideoPresenter.View>(), FeedEntityHolder {

   @Inject internal lateinit var router: Router
   @Inject internal lateinit var feedInteractor: FeedInteractor
   @Inject internal lateinit var flagsInteractor: FlagsInteractor
   @Inject internal lateinit var fragmentManager: FragmentManager
   @Inject internal lateinit var feedEntityHolderDelegate: FeedEntityHolderDelegate
   private lateinit var flagDelegate: FlagDelegate

   override fun onInjected() {
      super.onInjected()
      flagDelegate = FlagDelegate(flagsInteractor)
   }

   override fun takeView(view: FullscreenVideoPresenter.View) {
      super.takeView(view)
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer<Any>(), Action2(this::handleError))
      view.setVideoThumbnail(video.thumbnail)
      view.setSocialInfo(video, enableEdit(), enableDelete())
      loadEntity()
   }

   private fun loadEntity() {
      feedInteractor.videoCommandPipe
            .createObservable(GetVideoCommand(video.uid))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetVideoCommand>()
                  .onSuccess { updateFeedEntity(it.result) }
                  .onFail(this::handleError))
   }

   fun onLike() = feedInteractor.changeFeedEntityLikedStatusPipe().send(ChangeFeedEntityLikedStatusCommand(video))

   fun onComment() =
         NavigationWrapperFactory().componentOrDialogNavigationWrapper(router, fragmentManager, view)
               .navigate(CommentableFragment::class.java, CommentsBundle(video, false, true))

   fun onDelete() = feedInteractor.deleteVideoPipe().send(DeleteVideoCommand(video))

   fun onFlag(flaggable: Flaggable) {
      view.showFlagProgress()
      flagDelegate.loadFlags(flaggable, Action2 { command, throwable ->
         view.hideFlagProgress()
         handleError(command, throwable)
      })
   }

   fun sendFlagAction(flagReasonId: Int, reason: String) =
         flagDelegate.flagItem(FlagData(video.uid, flagReasonId, reason), view, Action2(this::handleError))

   fun playVideoRequired() = view.playVideo(video)

   override fun updateFeedEntity(updatedFeedEntity: FeedEntity) {
      if (updatedFeedEntity.uid == video.uid && updatedFeedEntity is Video) {
         video = updatedFeedEntity
         view.setSocialInfo(video, enableEdit(), enableDelete())
      }
   }

   override fun deleteFeedEntity(deletedFeedEntity: FeedEntity) {
      // we delete entity in TripImagesViewPagerPresenter
   }

   private fun enableEdit() = account != video.owner

   private fun enableDelete() = account == video.owner

   fun openUser() = view.openUser(UserBundle(video.owner))

   interface View : Presenter.View, Flaggable, FlagDelegate.View {
      fun setVideoThumbnail(videoThumbnail: String)

      fun openUser(bundle: UserBundle)

      fun setSocialInfo(video: Video, enableFlagging: Boolean, enableDelete: Boolean)

      fun showFlagProgress()

      fun hideFlagProgress()

      fun playVideo(video: Video)
   }
}

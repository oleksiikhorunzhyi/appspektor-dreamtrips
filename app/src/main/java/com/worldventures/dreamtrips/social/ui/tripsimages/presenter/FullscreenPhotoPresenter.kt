package com.worldventures.dreamtrips.social.ui.tripsimages.presenter

import android.support.v4.app.FragmentManager
import android.text.TextUtils
import com.worldventures.core.model.ShareType
import com.worldventures.core.utils.LocaleHelper
import com.worldventures.dreamtrips.core.navigation.router.Router
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.feed.bundle.CommentsBundle
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityHolder
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.TranslationFeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetPhotoCommand
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.CommentableFragment
import com.worldventures.dreamtrips.social.ui.flags.model.FlagData
import com.worldventures.dreamtrips.social.ui.flags.service.FlagDelegate
import com.worldventures.dreamtrips.social.ui.flags.service.FlagsInteractor
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageDeleteAnalyticsEvent
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageEditAnalyticsEvent
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageFlagAnalyticsEvent
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageShareAnalyticsEvent
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoTagsCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.TranslatePhotoCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate.DownloadImageDelegate
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.EditPhotoBundle
import io.techery.janet.helper.ActionStateSubscriber
import rx.functions.Action2
import java.io.IOException
import javax.inject.Inject

class FullscreenPhotoPresenter(private var photo: Photo) : Presenter<FullscreenPhotoPresenter.View>(), FeedEntityHolder {

   @Inject internal lateinit var router: Router
   @Inject internal lateinit var fm: FragmentManager
   @Inject internal lateinit var feedInteractor: FeedInteractor
   @Inject internal lateinit var flagsInteractor: FlagsInteractor
   @Inject internal lateinit var tripImagesInteractor: TripImagesInteractor
   @Inject internal lateinit var feedEntityHolderDelegate: FeedEntityHolderDelegate
   @Inject internal lateinit var translationFeedInteractor: TranslationFeedInteractor
   @Inject internal lateinit var downloadImageDelegate: DownloadImageDelegate

   private lateinit var flagDelegate: FlagDelegate

   override fun onInjected() {
      super.onInjected()
      flagDelegate = FlagDelegate(flagsInteractor)
   }

   override fun onViewTaken() {
      super.onViewTaken()
      setupTranslationState()
      subscribeToTranslation()
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer<Any>(), Action2(this::handleError))
      // we have null image path when getting photo from push notification, wait until entity is loaded by UID then
      if (photo.imagePath != null) {
         view.setPhoto(photo)
      }
      loadEntity()
   }

   private fun setupTranslationState() {
      val ownPost = photo.owner != null && photo.owner.id == appSessionHolder.get().get().user().id
      val emptyPostText = TextUtils.isEmpty(photo.title)
      val ownLanguage = LocaleHelper.isOwnLanguage(appSessionHolder, photo.language)
      val emptyPostLanguage = TextUtils.isEmpty(photo.language)
      if (ownPost || emptyPostText || ownLanguage || emptyPostLanguage) {
         view.hideTranslationButton()
      } else {
         view.showTranslationButton()
      }
   }

   private fun loadEntity() {
      feedInteractor.photoCommandPipe
            .createObservable(GetPhotoCommand(photo.uid))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetPhotoCommand>()
                  .onSuccess { updateFeedEntity(it.result) }
                  .onFail(this::handleError))
   }

   fun shouldSyncTags() = view.syncTagViewGroupWithGlobalState(photo)

   fun onUserClicked() = view.openUser(UserBundle(photo.owner))

   fun onDeleteAction() = tripImagesInteractor.deletePhotoPipe.send(DeletePhotoCommand(photo))

   fun deleteTag(tag: PhotoTag) {
      tripImagesInteractor.deletePhotoTagsPipe
            .createObservable(DeletePhotoTagsCommand(photo.uid, listOf(tag.user
                  .id)))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<DeletePhotoTagsCommand>()
                  .onSuccess {
                     photo.photoTags.remove(tag)
                     photo.photoTagsCount = photo.photoTags.size
                  }
                  .onFail({ action, error -> this.handleError(action, error) }))
   }

   fun sendFlagAction(flagReasonId: Int, reason: String) =
         flagDelegate.flagItem(FlagData(photo.uid, flagReasonId, reason), view, Action2(this::handleError))

   fun onCommentsAction() =
         NavigationWrapperFactory().componentOrDialogNavigationWrapper(router, fm, view)
               .navigate(CommentableFragment::class.java, CommentsBundle(photo, false, true))

   fun onLikeAction() =
         feedInteractor.changeFeedEntityLikedStatusPipe().send(ChangeFeedEntityLikedStatusCommand(photo))

   fun onEdit() {
      if (view != null) {
         analyticsInteractor.analyticsActionPipe().send(TripImageEditAnalyticsEvent(photo.uid))
         view.editPhoto(EditPhotoBundle(photo))
      }
   }

   fun onFlagAction(flaggable: Flaggable) {
      analyticsInteractor.analyticsActionPipe().send(TripImageFlagAnalyticsEvent(photo.uid))
      view.showFlagProgress()
      flagDelegate.loadFlags(flaggable, Action2 { command, throwable ->
         view.hideFlagProgress()
         handleError(command, throwable)
      })
   }

   fun onDelete() = analyticsInteractor.analyticsActionPipe().send(TripImageDeleteAnalyticsEvent(photo.uid))

   fun onTranslateClicked() = translationFeedInteractor.translatePhotoPipe().send(TranslatePhotoCommand(photo))

   private fun subscribeToTranslation() {
      translationFeedInteractor.translatePhotoPipe()
            .observe()
            .filter { commandState -> commandState.action.photo == photo }
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<TranslatePhotoCommand>()
                  .onStart { view.showTranslationInProgress() }
                  .onSuccess { view.showTranslation(it.result, it.photo.language) }
                  .onFail { action, throwable ->
                     handleError(action, throwable)
                     view.showTranslationButton()
                  })
   }

   override fun updateFeedEntity(updatedPhoto: FeedEntity) {
      if (updatedPhoto == photo && updatedPhoto is Photo) {
         photo = updatedPhoto
         view.setPhoto(photo)
         setupTranslationState()
      }
   }

   override fun deleteFeedEntity(deletedFeedEntity: FeedEntity) {
      // we delete entity in TripImagesViewPagerPresenter
   }

   fun onShareAction() {
      if (!isConnected) {
         reportNoConnectionWithOfflineErrorPipe(IOException())
         return
      }

      analyticsInteractor.analyticsActionPipe().send(TripImageShareAnalyticsEvent(photo.uid))
      view.onShowShareOptions()
   }

   fun onShareOptionChosen(@ShareType type: String) {
      if (type == ShareType.EXTERNAL_STORAGE) {
         if (view.isVisibleOnScreen) {
            val imagePath = photo.imagePath
            if (imagePath != null) {
               downloadImageDelegate.downloadImage(imagePath, bindViewToMainComposer(), Action2(this::handleError))
            }
         }
      } else {
         view.openShare(photo.imagePath, photo.title, type)
      }
   }

   interface View : Presenter.View, FlagDelegate.View {
      fun setPhoto(photo: Photo)

      fun openUser(bundle: UserBundle)

      fun onShowShareOptions()

      fun openShare(imageUrl: String?, text: String?, @ShareType type: String)

      fun syncTagViewGroupWithGlobalState(photo: Photo?)

      fun editPhoto(bundle: EditPhotoBundle)

      fun showFlagProgress()

      fun hideFlagProgress()

      fun showContentWrapper()

      fun showTranslationButton()

      fun hideTranslationButton()

      fun showTranslation(translation: String, languageFrom: String?)

      fun showTranslationInProgress()
   }
}

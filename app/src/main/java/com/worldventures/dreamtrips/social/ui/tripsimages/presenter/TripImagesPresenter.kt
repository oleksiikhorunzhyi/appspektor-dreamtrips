package com.worldventures.dreamtrips.social.ui.tripsimages.presenter

import com.innahema.collections.query.queriables.Queryable
import com.worldventures.core.modules.picker.helper.PickerPermissionChecker
import com.worldventures.core.modules.picker.model.MediaPickerAttachment
import com.worldventures.core.ui.util.permission.PermissionUtils
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.UploadTripImageAnalyticAction
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost
import com.worldventures.dreamtrips.social.ui.feed.model.uploading.UploadingPostsList
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video
import com.worldventures.dreamtrips.social.ui.feed.presenter.UploadingListenerPresenter
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.UploadingPresenterDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostCreatedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.model.VideoMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImageArgsFilterFunc
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CheckVideoProcessingStatusCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesAddedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesRemovedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.TripImagesCommandFactory
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.UserImagesRemovedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent
import io.techery.janet.helper.ActionStateSubscriber
import rx.functions.Action2
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

typealias FeedEntityListener = com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityHolder

open class TripImagesPresenter(internal var tripImagesArgs: TripImagesArgs) : Presenter<TripImagesPresenter.View>(),
      UploadingListenerPresenter, FeedEntityListener {

   @Inject internal lateinit var tripImagesInteractor: TripImagesInteractor
   @Inject internal lateinit var postsInteractor: PostsInteractor
   @Inject internal lateinit var compoundOperationsInteractor: CompoundOperationsInteractor
   @Inject internal lateinit var uploadingPresenterDelegate: UploadingPresenterDelegate
   @Inject internal lateinit var tripImagesCommandFactory: TripImagesCommandFactory
   @Inject internal lateinit var feedEntityHolderDelegate: FeedEntityHolderDelegate
   @Inject internal lateinit var appConfigurationInteractor: AppConfigurationInteractor
   @Inject internal lateinit var pickerPermissionChecker: PickerPermissionChecker
   @Inject internal lateinit var permissionUtils: PermissionUtils

   internal var loading = false
   internal var lastPageReached = false
   internal var memberImagesAreRefreshing = false

   var currentItems: ArrayList<BaseMediaEntity<out FeedEntity>> = arrayListOf()
   internal var compoundOperationModels: List<PostCompoundOperationModel<*>> = listOf()

   override fun onInjected() {
      super.onInjected()
      pickerPermissionChecker.registerCallback(
            this::showMediaPicker,
            { view.showPermissionDenied(PickerPermissionChecker.PERMISSIONS) },
            { view.showPermissionExplanationText(PickerPermissionChecker.PERMISSIONS) })
   }

   override fun onViewTaken() {
      super.onViewTaken()
      updateItemsInView()
      initCreateMediaFlow()
      subscribeToTripImages()
      subscribeToPhotoDeletedEvents()
      subscribeToErrorUpdates()
      subscribeToNewItems()
      refreshImages()
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer<Any>(), Action2(this::handleError))
   }

   fun reload() {
      view.hideNewImagesButton()
      refreshImages()
   }

   fun onItemClick(entity: BaseMediaEntity<*>) = view.openFullscreen(lastPageReached, currentItems.indexOf(entity))

   fun recheckPermission(permissions: Array<String>, userAnswer: Boolean) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionChecker.recheckPermission(userAnswer)
      }
   }

   fun addPhotoClicked() = pickerPermissionChecker.checkPermission()

   private fun showMediaPicker() {
      appConfigurationInteractor.configurationPipe
            .createObservableResult(ConfigurationCommand())
            .compose(IoToMainComposer())
            .map { it.result.videoRequirement.videoMaxLength }
            .subscribe { length ->
               view.openPicker(length)
               trackUploadAnalyticEvent()
            }
   }

   private fun trackUploadAnalyticEvent() {
      val action = if (tripImagesArgs.tripImageType == TripImagesArgs.TripImageType.ACCOUNT_IMAGES)
         UploadTripImageAnalyticAction.fromMyImages()
      else UploadTripImageAnalyticAction.fromMemberImages()

      analyticsInteractor.analyticsActionPipe().send(action)
   }

   fun pickedAttachments(mediaAttachment: MediaPickerAttachment) = view.openCreatePhoto(mediaAttachment)

   internal fun initCreateMediaFlow() {
      if (tripImagesArgs.tripImageType != TripImagesArgs.TripImageType.MEMBER_IMAGES && tripImagesArgs.userId != account.id) {
         view.hideCreateImageButton()
      } else {
         subscribeToBackgroundUploadingOperations()
      }
   }

   internal fun refreshImages() = tripImagesInteractor.baseTripImagesPipe.send(tripImagesCommandFactory.provideCommand(tripImagesArgs))

   fun loadNext() {
      if (lastPageReached || loading || currentItems.isEmpty()) return

      loading = true
      tripImagesInteractor.baseTripImagesPipe
            .send(tripImagesCommandFactory.provideLoadMoreCommand(tripImagesArgs, currentItems))
   }

   internal fun subscribeToTripImages() {
      tripImagesInteractor.baseTripImagesPipe
            .observe()
            .filter(TripImageArgsFilterFunc(tripImagesArgs))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<BaseMediaCommand>()
                  .onStart {
                     loading = true
                     view.showLoading()
                  }
                  .onProgress { command, _ ->
                     if (command.isReload && !command.items.isEmpty()) {
                        itemsUpdated(command)
                     }
                  }
                  .onSuccess(this::itemsUpdated)
                  .onFail { baseTripImagesCommand, throwable ->
                     loading = false
                     view.finishLoading()
                     handleError(baseTripImagesCommand, throwable)
                  }
            )
   }

   private fun itemsUpdated(baseMediaCommand: BaseMediaCommand) {
      Timber.d("Load -- loaded")
      loading = false
      lastPageReached = baseMediaCommand.lastPageReached()
      if (baseMediaCommand.fromCacheOnly || baseMediaCommand.result != null) {
         view.finishLoading()
      }
      if (baseMediaCommand.isReload) {
         currentItems.clear()
      }
      currentItems.addAll(baseMediaCommand.items)
      updateItemsInView()
      tripImagesInteractor.checkVideoProcessingStatusPipe.send(CheckVideoProcessingStatusCommand(currentItems))
   }

   internal fun subscribeToPhotoDeletedEvents() {
      tripImagesInteractor.deletePhotoPipe
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .map { it.result.uid }
            .subscribe { deletedItemUid ->
               currentItems = ArrayList(currentItems.filter { it.item.uid != deletedItemUid })
               updateItemsInView()
            }
   }

   internal fun subscribeToErrorUpdates() =
         offlineErrorInteractor.offlineErrorCommandPipe()
               .observeSuccess()
               .compose(bindViewToMainComposer())
               .subscribe { reportNoConnection() }

   internal fun subscribeToNewItems() =
         postsInteractor.postCreatedPipe()
               .observeSuccess()
               .compose(bindViewToMainComposer())
               .map(PostCreatedCommand::getResult)
               .subscribe(this::onFeedItemAdded)

   private fun onFeedItemAdded(textualPost: TextualPost) {
      if (textualPost.attachments.isEmpty()) return

      var mediaEntities = textualPost.attachments
            .map(this::fromFeedEntityHolder)
            .filter({ mediaEntity -> !currentItems.contains(mediaEntity) })
            .toList()
      val allPhotosHavePublishAt = mediaEntities
            .count({ it.item.createdAt == null }) == 0
      mediaEntities = if (allPhotosHavePublishAt) {
         mediaEntities.sortedBy { it.item.createdAt }
      } else {
         mediaEntities.reversed()
      }

      tripImagesInteractor.memberImagesAddedCommandPipe
            .send(MemberImagesAddedCommand(tripImagesArgs, mediaEntities))
      currentItems.addAll(0, mediaEntities)
      updateItemsInView()
      view.scrollToTop()
   }

   private fun fromFeedEntityHolder(feedEntityHolder: FeedEntityHolder<*>): BaseMediaEntity<*> {
      val feedEntityHolderItem = feedEntityHolder.item
      feedEntityHolderItem.owner = account
      when (feedEntityHolder.type) {
         FeedEntityHolder.Type.PHOTO -> {
            if (feedEntityHolderItem is Photo) {
               return PhotoMediaEntity(feedEntityHolderItem)
            } else {
               throw Exception("Cannot find photo in FeedEntityHolder")
            }
         }
         FeedEntityHolder.Type.VIDEO -> {
            if (feedEntityHolderItem is Video) {
               return VideoMediaEntity(feedEntityHolderItem)
            } else {
               throw Exception("Cannot find video in FeedEntityHolder")
            }
         }
         else -> throw Exception("Cannot map type")
      }
   }

   internal fun updateItemsInView() {
      val items = arrayListOf<Any>()
      if (!compoundOperationModels.isEmpty()) {
         items.add(UploadingPostsList(compoundOperationModels))
      }
      items.addAll(currentItems)
      view.updateItems(items)
   }

   ///////////////////////////////////////////////////////////////////////////
   // Uploading handling
   ///////////////////////////////////////////////////////////////////////////

   internal fun subscribeToBackgroundUploadingOperations() {
      compoundOperationsInteractor.compoundOperationsPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<CompoundOperationsCommand>()
                  .onSuccess { compoundOperationsCommand ->
                     compoundOperationModels = Queryable.from(compoundOperationsCommand.result)
                           .cast(PostCompoundOperationModel::class.java).toList()
                     updateItemsInView()
                  })
   }

   override fun onUploadResume(compoundOperationModel: PostCompoundOperationModel<*>) =
         uploadingPresenterDelegate.onUploadResume(compoundOperationModel)

   override fun onUploadPaused(compoundOperationModel: PostCompoundOperationModel<*>) =
         uploadingPresenterDelegate.onUploadPaused(compoundOperationModel)

   override fun onUploadRetry(compoundOperationModel: PostCompoundOperationModel<*>) =
         uploadingPresenterDelegate.onUploadRetry(compoundOperationModel)

   override fun onUploadCancel(compoundOperationModel: PostCompoundOperationModel<*>) =
         uploadingPresenterDelegate.onUploadCancel(compoundOperationModel)

   override fun updateFeedEntity(updatedFeedEntity: FeedEntity) {
      currentItems.filter { it.item.uid == updatedFeedEntity.uid }
            .forEach {
               if (it is PhotoMediaEntity && updatedFeedEntity is Photo) {
                  it.item = updatedFeedEntity
               } else if (it is VideoMediaEntity && updatedFeedEntity is Video) {
                  it.item = updatedFeedEntity
               }
            }
      updateItemsInView()
   }

   override fun deleteFeedEntity(feedEntity: FeedEntity) {
      currentItems = ArrayList(currentItems
            .filter { mediaEntity -> mediaEntity.item.uid != feedEntity.uid }
            .toList())

      val deletedEntity: BaseMediaEntity<*> = when (feedEntity) {
         is Photo -> {
            val photoMediaEntity = PhotoMediaEntity(feedEntity)
            photoMediaEntity
         }
         is Video -> {
            val videoMediaEntity = VideoMediaEntity(feedEntity)
            videoMediaEntity
         }
         else -> return
      }
      when (tripImagesArgs.tripImageType) {
         TripImagesArgs.TripImageType.MEMBER_IMAGES -> tripImagesInteractor.memberImagesRemovedPipe
               .send(MemberImagesRemovedCommand(tripImagesArgs, listOf(deletedEntity)))
         TripImagesArgs.TripImageType.ACCOUNT_IMAGES -> tripImagesInteractor.userImagesRemovedPipe
               .send(UserImagesRemovedCommand(tripImagesArgs, listOf(deletedEntity)))
      }
      updateItemsInView()
   }

   interface View : Presenter.View, PermissionUIComponent {
      fun scrollToTop()

      fun openFullscreen(lastPageReached: Boolean, index: Int)

      fun updateItems(items: List<*>)

      fun showLoading()

      fun finishLoading()

      fun openPicker(durationLimit: Int)

      fun showNewImagesButton(newImagesCountString: String)

      fun hideNewImagesButton()

      fun hideCreateImageButton()

      fun openCreatePhoto(mediaAttachment: MediaPickerAttachment)
   }
}

package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.picker.helper.PickerPermissionChecker
import com.worldventures.core.modules.picker.model.MediaPickerAttachment
import com.worldventures.core.ui.util.permission.PermissionUtils
import com.worldventures.core.utils.DateTimeUtils
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.model.CategoryItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.AdobeStartUploadBucketPhotoAction
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.ApptentiveStartUploadBucketPhotoAction
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.AddBucketItemPhotoCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetCategoriesCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketPostBody
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent
import com.worldventures.wallet.util.WalletFilesUtils
import io.techery.janet.CancelException
import io.techery.janet.helper.ActionStateSubscriber
import rx.schedulers.Schedulers
import java.util.Calendar
import java.util.Date
import java.util.HashSet
import javax.inject.Inject

open class BucketItemEditPresenter(type: BucketItem.BucketType, bucketItem: BucketItem, ownerId: Int)
   : BucketDetailsBasePresenter<BucketItemEditPresenter.View, EntityStateHolder<BucketPhoto>>(type, bucketItem, ownerId) {

   @Inject internal lateinit var pickerPermissionChecker: PickerPermissionChecker
   @Inject internal lateinit var permissionUtils: PermissionUtils

   var selectedDate: Date? = null
   internal var savingItem = false
   internal val operationList: MutableSet<AddBucketItemPhotoCommand> = HashSet()

   val datePickerDate: Date
      get() = if (bucketItem.targetDate != null) {
         bucketItem.targetDate
      } else {
         Calendar.getInstance().time
      }

   override fun onInjected() {
      super.onInjected()
      pickerPermissionChecker.registerCallback(
            { view.showMediaPicker() },
            { view.showPermissionDenied(PickerPermissionChecker.PERMISSIONS) },
            { view.showPermissionExplanationText(PickerPermissionChecker.PERMISSIONS) })
   }

   override fun onViewTaken() {
      super.takeView(view)
      subscribeToAddingPhotos()
   }

   override fun onResume() {
      super.onResume()
      selectedDate = bucketItem.targetDate
      loadCategories()
   }

   internal fun loadCategories() =
      bucketInteractor.categoriesPipe
            .createObservable(GetCategoriesCommand())
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetCategoriesCommand>()
                  .onSuccess { categoriesLoaded(it.result) }
                  .onFail(this::handleError))

   private fun categoriesLoaded(categoryItems: List<CategoryItem>) {
      view.setCategoryItems(categoryItems, bucketItem.category)
      mergeBucketItemPhotosWithStorage()
   }

   internal fun mergeBucketItemPhotosWithStorage() =
      bucketInteractor.mergeBucketItemPhotosWithStorageCommandPipe()
            .createObservableResult(MergeBucketItemPhotosWithStorageCommand(bucketItem.uid, bucketItem
                  .photos))
            .map { it.result }
            .compose(bindView())
            .observeOn(Schedulers.immediate())
            .subscribe { entityStateHolders ->
               putCoverPhotoAsFirst(bucketItem.photos)
               view.setImages(entityStateHolders)
            }

   fun openPickerRequired() = pickerPermissionChecker.checkPermission()

   fun recheckPermission(permissions: Array<String>, userAnswer: Boolean) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionChecker.recheckPermission(userAnswer)
      }
   }

   override fun handleError(action: Any, error: Throwable) {
      super.handleError(action, error)
      view.hideLoading()
   }

   fun saveItem() {
      view.showLoading()
      savingItem = true

      bucketInteractor.updatePipe()
            .createObservable(UpdateBucketItemCommand(createBucketPostBody()))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<UpdateBucketItemCommand>().onSuccess {
               if (savingItem) {
                  savingItem = false
                  view.done()
               }
            }.onFail { updateItemHttpAction, throwable ->
               view.hideLoading()
               super.handleError(updateItemHttpAction, throwable)
            })
   }

   fun deletePhotoRequest(bucketPhoto: BucketPhoto) {
      if (bucketItem.photos.isEmpty() || !bucketItem.photos.contains(bucketPhoto)) {
         return
      }
      bucketInteractor.deleteItemPhotoPipe()
            .createObservable(DeleteItemPhotoCommand(bucketItem, bucketPhoto))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<DeleteItemPhotoCommand>()
                  .onSuccess { view.deleteImage(EntityStateHolder.create(bucketPhoto, EntityStateHolder.State.DONE)) }
                  .onFail(this::handleError))
   }

   fun onDateSet(year: Int, month: Int, day: Int) {
      val dateString = DateTimeUtils.convertDateToString(year, month, day)
      view.setTime(dateString)
      selectedDate = DateTimeUtils.dateFromString(dateString)
   }

   fun onDateClear() {
      view.setTime(context.getString(R.string.someday))
      selectedDate = null
   }

   fun onPhotoCellClicked(photoStateHolder: EntityStateHolder<BucketPhoto>) {
      val state = photoStateHolder.state()
      when (state) {
         EntityStateHolder.State.FAIL -> {
            view.deleteImage(photoStateHolder)
            startUpload(photoStateHolder.entity().imagePath)
         }
         EntityStateHolder.State.PROGRESS -> {
            view.deleteImage(photoStateHolder)
            cancelUpload(photoStateHolder)
         }
         else -> {
         }
      }
   }

   internal fun startUpload(path: String) {
      analyticsInteractor.analyticsActionPipe().send(ApptentiveStartUploadBucketPhotoAction())
      analyticsInteractor.analyticsActionPipe().send(AdobeStartUploadBucketPhotoAction(bucketItem.uid))
      bucketInteractor.addBucketItemPhotoPipe().send(AddBucketItemPhotoCommand(bucketItem, path))
   }

   internal fun cancelUpload(photoStateHolder: EntityStateHolder<BucketPhoto>) {
      val addBucketItemPhotoCommand = findCommandByStateHolder(photoStateHolder)
      if (addBucketItemPhotoCommand != null) {
         bucketInteractor.addBucketItemPhotoPipe().cancel(addBucketItemPhotoCommand)
      }
   }

   fun imageSelected(mediaPickerAttachment: MediaPickerAttachment) =
         mediaPickerAttachment.chosenImages
               .map { pickerModel -> WalletFilesUtils.convertPickedPhotoToUri(pickerModel).toString() }
               .forEach(this::startUpload)

   internal fun subscribeToAddingPhotos() =
      bucketInteractor.addBucketItemPhotoPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<AddBucketItemPhotoCommand>().onStart { command ->
               operationList.add(command)
               view.addItemInProgressState(command.photoEntityStateHolder())
            }.onSuccess { command ->
               operationList.remove(command)
               view.changeItemState(command.photoEntityStateHolder())
            }.onFail { command, throwable ->
               operationList.remove(command)
               if (throwable !is CancelException) {
                  view.changeItemState(command.photoEntityStateHolder())
               }
            })

   private fun createBucketPostBody() =
      ImmutableBucketPostBody.builder()
            .id(bucketItem.uid)
            .name(view.title)
            .description(view.description)
            .status(if (view.status) BucketItem.COMPLETED else BucketItem.NEW)
            .tags(view.tags)
            .friends(view.people)
            .categoryId(view.selectedItem?.id ?: 0)
            .date(selectedDate)
            .build()

   private fun findCommandByStateHolder(photoEntityStateHolder: EntityStateHolder<BucketPhoto>) =
         operationList.firstOrNull { it.photoEntityStateHolder() == photoEntityStateHolder }

   interface View : BucketDetailsBasePresenter.View<EntityStateHolder<BucketPhoto>>, PermissionUIComponent {

      val selectedItem: CategoryItem?

      val status: Boolean

      val tags: List<String>

      val people: List<String>

      val title: String

      val description: String

      fun showError()

      fun setCategoryItems(items: List<CategoryItem>, selectedItem: CategoryItem?)

      fun showMediaPicker()

      fun showLoading()

      fun hideLoading()

      fun addItemInProgressState(photoEntityStateHolder: EntityStateHolder<BucketPhoto>)

      fun changeItemState(photoEntityStateHolder: EntityStateHolder<BucketPhoto>)

      fun deleteImage(photoStateHolder: EntityStateHolder<BucketPhoto>)
   }
}

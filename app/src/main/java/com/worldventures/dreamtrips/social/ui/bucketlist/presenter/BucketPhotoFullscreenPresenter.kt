package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketCoverBody
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class BucketPhotoFullscreenPresenter(private val bucketPhoto: BucketPhoto, private var bucketItem: BucketItem)
   : Presenter<BucketPhotoFullscreenPresenter.View>() {
   @Inject internal lateinit var bucketInteractor: BucketInteractor

   override fun onViewTaken() {
      super.onViewTaken()
      updatePhoto()
      view.setBucketPhoto(bucketPhoto)
      subscribeToBucketUpdates()
   }

   internal fun updatePhoto() {
      if (account == bucketItem.owner) {
         val coverPhoto = bucketItem.coverPhoto
         view.updateCoverCheckbox(coverPhoto != null && coverPhoto == bucketPhoto)
         view.showDeleteBtn()
      } else {
         view.hideDeleteBtn()
         view.hideCoverCheckBox()
      }
   }

   internal fun subscribeToBucketUpdates() =
         bucketInteractor.updatePipe()
         .observeSuccess()
         .map { it.result }
         .compose(bindViewToMainComposer())
         .subscribe {
            bucketItem = it
            view.updateCoverCheckbox(bucketItem.coverPhoto != null && bucketItem.coverPhoto == bucketPhoto)
         }

   fun onDeletePhoto() =
         bucketInteractor.deleteItemPhotoPipe()
            .createObservable(DeleteItemPhotoCommand(bucketItem, bucketPhoto))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<DeleteItemPhotoCommand>()
                  .onSuccess { view.informUser(context.getString(R.string.photo_deleted)) }
                  .onFail(this::handleError))

   fun onChangeCoverChosen() =
      bucketInteractor.updatePipe()
            .createObservable(UpdateBucketItemCommand(ImmutableBucketCoverBody.builder()
                  .id(bucketItem.uid)
                  .status(bucketItem.status)
                  .type(bucketItem.type)
                  .coverId(bucketPhoto.uid)
                  .build()))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<UpdateBucketItemCommand>()
                  .onStart { view.showCoverProgress() }
                  .onSuccess { view.hideCoverProgress() }
                  .onFail { itemAction, throwable ->
                     view.hideCoverProgress()
                     handleError(itemAction, throwable)
                  })

   interface View : Presenter.View {
      fun setBucketPhoto(bucketPhoto: BucketPhoto)

      fun updateCoverCheckbox(currentCover: Boolean)

      fun showCoverProgress()

      fun hideCoverProgress()

      fun hideDeleteBtn()

      fun showDeleteBtn()

      fun hideCoverCheckBox()
   }
}

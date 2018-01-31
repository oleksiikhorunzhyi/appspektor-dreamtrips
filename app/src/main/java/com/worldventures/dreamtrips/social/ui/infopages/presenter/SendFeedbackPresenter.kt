package com.worldventures.dreamtrips.social.ui.infopages.presenter

import android.net.Uri
import android.os.Bundle
import com.worldventures.core.janet.composer.ActionPipeCacheWiper
import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.bundle.FeedbackImageAttachmentsBundle
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.core.modules.infopages.model.FeedbackType
import com.worldventures.core.modules.infopages.service.FeedbackAttachmentsManager
import com.worldventures.core.modules.infopages.service.FeedbackInteractor
import com.worldventures.core.modules.infopages.service.analytics.SendFeedbackAnalyticAction
import com.worldventures.core.modules.infopages.service.command.AttachmentsRemovedCommand
import com.worldventures.core.modules.infopages.service.command.GetFeedbackCommand
import com.worldventures.core.modules.infopages.service.command.SendFeedbackCommand
import com.worldventures.core.modules.infopages.service.command.UploadFeedbackAttachmentCommand
import com.worldventures.core.modules.picker.helper.PickerPermissionChecker
import com.worldventures.core.modules.picker.model.MediaPickerAttachment
import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.core.ui.util.permission.PermissionUtils
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent
import io.techery.janet.ActionState
import io.techery.janet.helper.ActionStateSubscriber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func4
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class SendFeedbackPresenter : Presenter<SendFeedbackPresenter.View>() {

   @Inject lateinit var feedbackInteractor: FeedbackInteractor
   @Inject lateinit var pickerPermissionChecker: PickerPermissionChecker
   @Inject lateinit var permissionUtils: PermissionUtils
   private val attachmentsManager = FeedbackAttachmentsManager()

   override fun onInjected() {
      super.onInjected()
      pickerPermissionChecker.registerCallback(
            { view.showMediaPicker(PICKER_MAX_IMAGES - attachmentsManager.attachments.size) },
            { view.showPermissionDenied(PickerPermissionChecker.PERMISSIONS) },
            { view.showPermissionExplanationText(PickerPermissionChecker.PERMISSIONS) })
   }

   override fun takeView(view: View) {
      super.takeView(view)
      getFeedbackReasons(view)
      subscribeToFormValidation()
      subscribeToAttachments()
   }

   override fun onStart() {
      super.onStart()
      subscribeToUploadingAttachments()
   }

   override fun restoreInstanceState(savedState: Bundle?) {
      super.restoreInstanceState(savedState)
      attachmentsManager.restoreInstanceState(savedState)
   }

   override fun saveInstanceState(savedState: Bundle?) {
      super.saveInstanceState(savedState)
      attachmentsManager.saveInstanceState(savedState)
   }

   private fun getImageAttachments() = attachmentsManager.attachments.map {
      EntityStateHolder.create(it.entity(), it.state()).entity()
   }.toList()

   private fun getFeedbackReasons(view: View) {
      feedbackInteractor.feedbackPipe
            .createObservable(GetFeedbackCommand())
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetFeedbackCommand>()
                  .onStart {
                     it.items().apply {
                        view.setFeedbackTypes(this)
                        if (this?.isNotEmpty() != true) view.showProgressBar()
                     }
                  }
                  .onFinish {
                     view.hideProgressBar()
                     view.setFeedbackTypes(it.items())
                  }
                  .onFail(this::handleError))
   }

   fun sendFeedback(feedbackType: Int, text: String) {
      analyticsInteractor.analyticsActionPipe()
            .send(SendFeedbackAnalyticAction(feedbackType, getImageAttachments().size))

      view.changeDoneButtonState(false)

      feedbackInteractor.sendFeedbackPipe()
            .createObservable(SendFeedbackCommand(feedbackType, text, getImageAttachments()))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<SendFeedbackCommand>()
                  .onSuccess {
                     attachmentsManager.removeAll()
                     view.feedbackSent()
                  }
                  .onFail { sendFeedbackCommand, throwable ->
                     view.changeDoneButtonState(true)
                     handleError(sendFeedbackCommand, throwable)
                  })
   }

   private fun subscribeToFormValidation() {
      Observable.combineLatest(
            view.getFeedbackTypeSelectedObservable(),
            view.getMessageTextObservable(),
            view.getPhotoPickerVisibilityObservable(),
            getAttachmentsObservable(), validateForm())
            .compose(bindView())
            .subscribe(view::changeDoneButtonState)
   }

   private fun getAttachmentsObservable() = attachmentsManager.attachmentsObservable
         .startWith(Observable.just<EntityStateHolder<FeedbackImageAttachment>>(null))

   private fun validateForm() = Func4<FeedbackType, CharSequence, Boolean, Any, Boolean>
   { feedbackType, message, photoPickerVisible, _ ->
      return@Func4 (feedbackType?.id ?: return@Func4 false) > 0 &&
            !message.isNullOrBlank() &&
            !photoPickerVisible &&
            attachmentsManager.failedOrPendingAttachmentsCount <= 0
   }

   fun onShowMediaPicker() = pickerPermissionChecker.checkPermission()

   fun recheckPermission(permissions: Array<String>, userAnswer: Boolean) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionChecker.recheckPermission(userAnswer)
      }
   }

   fun imagesPicked(mediaPickerAttachment: MediaPickerAttachment) {
      mediaPickerAttachment.chosenImages.map(this::convertPhotoToUriPhoto).forEach(this::uploadImageAttachment)
   }

   private fun convertPhotoToUriPhoto(model: PhotoPickerModel) = model.uri.let { uri ->
      if (uri.scheme == null) File(model.uri.path).let { file ->
         if (file.exists()) Uri.fromFile(file)
         else Timber.e("Cannot parse path into Uri : %s", uri.path).let { uri }
      } else {
         uri
      }
   }.toString()

   fun onFeedbackAttachmentClicked(holder: EntityStateHolder<FeedbackImageAttachment>) {
      view.showAttachments(holder, FeedbackImageAttachmentsBundle(attachmentsManager
            .attachments.indexOf(holder), getImageAttachments()))
   }

   private fun uploadImageAttachment(path: String) {
      feedbackInteractor.uploadAttachmentPipe().send(UploadFeedbackAttachmentCommand(FeedbackImageAttachment(path)))
   }

   fun onRetryUploadingAttachment(holder: EntityStateHolder<FeedbackImageAttachment>) {
      onRemoveAttachment(holder)
      uploadImageAttachment(holder.entity().originalFilePath)
   }

   fun onRemoveAttachment(holder: EntityStateHolder<FeedbackImageAttachment>) {
      attachmentsManager.remove(holder)
      view.removeAttachment(holder)
   }

   private fun subscribeToAttachments() {
      view.setAttachments(attachmentsManager.attachments)

      attachmentsManager.attachmentsObservable
            .compose(bindView())
            .subscribe { view.changeAddPhotosButtonState(attachmentsManager.attachments.size < PICKER_MAX_IMAGES) }

      feedbackInteractor.attachmentsRemovedPipe()
            .observeWithReplay()
            .compose(ActionPipeCacheWiper(feedbackInteractor.attachmentsRemovedPipe()))
            .filter { it.status == ActionState.Status.SUCCESS }
            .map(ActionState<AttachmentsRemovedCommand>::action)
            .map(AttachmentsRemovedCommand::getResult)
            .compose(bindViewToMainComposer())
            .subscribe {
               attachmentsManager.attachments.forEach { holder ->
                  if (it.contains(holder.entity())) {
                     attachmentsManager.remove(holder)
                     view.removeAttachment(holder)
                  }
               }
            }
   }

   private fun subscribeToUploadingAttachments() {
      feedbackInteractor.uploadAttachmentPipe()
            .observeWithReplay()
            .compose(ActionPipeCacheWiper(feedbackInteractor.uploadAttachmentPipe()))
            .compose(bindUntilStop())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ActionStateSubscriber<UploadFeedbackAttachmentCommand>()
                  .onProgress { commandInProgress, _ -> updateImageAttachment(commandInProgress) }
                  .onSuccess(this::updateImageAttachment)
                  .onFail { failedCommand, throwable ->
                     updateImageAttachment(failedCommand)
                     handleError(failedCommand, throwable)
                  })
   }

   private fun updateImageAttachment(command: UploadFeedbackAttachmentCommand) = command.entityStateHolder.let {
      view.updateAttachment(it)
      attachmentsManager.update(it)
   }

   companion object {
      private const val PICKER_MAX_IMAGES = 5
   }

   interface View : Presenter.View, PermissionUIComponent {

      fun setFeedbackTypes(feedbackTypes: List<FeedbackType>)

      fun feedbackSent()

      fun showProgressBar()

      fun hideProgressBar()

      fun showMediaPicker(maxPhotos: Int)

      fun setAttachments(attachments: List<EntityStateHolder<FeedbackImageAttachment>>)

      fun updateAttachment(image: EntityStateHolder<FeedbackImageAttachment>)

      fun removeAttachment(image: EntityStateHolder<FeedbackImageAttachment>)

      fun showRetryUploadingUiForAttachment(attachmentHolder: EntityStateHolder<FeedbackImageAttachment>)

      fun showAttachments(holder: EntityStateHolder<FeedbackImageAttachment>, bundle: FeedbackImageAttachmentsBundle)

      fun getMessageTextObservable(): Observable<CharSequence>

      fun getFeedbackTypeSelectedObservable(): Observable<FeedbackType>

      fun getPhotoPickerVisibilityObservable(): Observable<Boolean>

      fun changeDoneButtonState(enable: Boolean)

      fun changeAddPhotosButtonState(enable: Boolean)
   }
}

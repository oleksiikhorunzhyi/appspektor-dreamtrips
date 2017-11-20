package com.worldventures.wallet.ui.settings.help.feedback.base.impl

import android.net.Uri
import com.worldventures.core.janet.composer.ActionPipeCacheWiper
import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.core.modules.infopages.service.CancelableFeedbackAttachmentsManager
import com.worldventures.core.modules.infopages.service.FeedbackInteractor
import com.worldventures.core.modules.infopages.service.command.AttachmentsRemovedCommand
import com.worldventures.core.modules.infopages.service.command.UploadFeedbackAttachmentCommand
import com.worldventures.core.modules.picker.command.MediaAttachmentPrepareCommand
import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.core.modules.picker.service.MediaPickerInteractor
import com.worldventures.wallet.domain.WalletConstants
import com.worldventures.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen
import io.techery.janet.ActionState
import io.techery.janet.helper.ActionStateSubscriber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class FeedbackAttachmentsPresenterDelegateImpl(private val mediaPickerInteractor: MediaPickerInteractor,
                                               private val feedbackInteractor: FeedbackInteractor,
                                               private val attachmentsManager: CancelableFeedbackAttachmentsManager) : FeedbackAttachmentsPresenterDelegate {

   override val imagesAttachments: List<FeedbackImageAttachment>
      get() = attachmentsManager.attachments.map { it.entity() }

   override val availableAttachmentsCount: Int
      get() = WalletConstants.WALLET_FEEDBACK_MAX_PHOTOS_ATTACHMENT - attachmentsCount

   override val hasFailedOrPendingAttachments: Boolean
      get() = attachmentsManager.failedOrPendingAttachmentsCount > 0

   override val attachmentsObservable: Observable<EntityStateHolder<FeedbackImageAttachment>>
      get() = attachmentsManager.attachmentsObservable

   private var attachmentsCount: Int = 0
   private lateinit var view: BaseFeedbackScreen

   override fun init(view: BaseFeedbackScreen) {
      this.view = view
      observeAttachmentsPreparation()
      observeAttachments()
   }

   override fun destroy() {
      attachmentsManager.cancelAll()
   }

   override fun findPosition(holder: EntityStateHolder<FeedbackImageAttachment>) = attachmentsManager.attachments.indexOf(holder)

   override fun fetchAttachments() {
      view.setAttachments(attachmentsManager.attachments)
   }

   override fun clearAttachments() {
      attachmentsManager.removeAll()
   }

   override fun retryUploadingAttachment(attachmentHolder: EntityStateHolder<FeedbackImageAttachment>) {
      removeAttachment(attachmentHolder)
      uploadImageAttachment(Uri.parse(attachmentHolder.entity().originalFilePath))
   }

   override fun removeAttachment(holder: EntityStateHolder<FeedbackImageAttachment>) {
      attachmentsManager.remove(holder)
      view.removeAttachment(holder)
   }

   override fun handleAttachedImages(chosenImages: List<PhotoPickerModel>) {
      mediaPickerInteractor.mediaAttachmentPreparePipe().send(MediaAttachmentPrepareCommand(chosenImages))
   }

   private fun observeAttachmentsPreparation() {
      mediaPickerInteractor.mediaAttachmentPreparePipe()
            .observe()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ActionStateSubscriber<MediaAttachmentPrepareCommand>()
                  .onSuccess { it.result.forEach { uploadImageAttachment(it) } }
                  .onFail { _, throwable -> Timber.e(throwable, "Cannot process attachments") })

   }

   private fun observeAttachments() {
      attachmentsManager.attachmentsObservable
            .compose(view.bindUntilDetach())
            .subscribe {
               this.attachmentsCount = attachmentsManager.attachments.size
               view.changeAddPhotosButtonEnabled(attachmentsCount < WalletConstants.WALLET_FEEDBACK_MAX_PHOTOS_ATTACHMENT)
            }

      feedbackInteractor.attachmentsRemovedPipe()
            .observeWithReplay()
            .compose<ActionState<AttachmentsRemovedCommand>>(ActionPipeCacheWiper<AttachmentsRemovedCommand>(feedbackInteractor.attachmentsRemovedPipe()))
            .filter { actionState -> actionState.status == ActionState.Status.SUCCESS }
            .map<List<FeedbackImageAttachment>> { actionState -> actionState.action.result }
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { removedAttachments ->
               attachmentsManager.attachments.forEach { holder ->
                  if (removedAttachments.contains(holder.entity())) {
                     attachmentsManager.remove(holder)
                     view.removeAttachment(holder)
                  }
               }
            }

      feedbackInteractor.uploadAttachmentPipe()
            .observeWithReplay()
            .compose(ActionPipeCacheWiper(feedbackInteractor.uploadAttachmentPipe()))
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ActionStateSubscriber<UploadFeedbackAttachmentCommand>()
                  .onStart { this.updateImageAttachment(it) }
                  .onProgress { command, _ -> updateImageAttachment(command) }
                  .onSuccess { this.onCommandFinished(it) }
                  .onFail { failedCommand, _ -> onCommandFinished(failedCommand) }
            )
   }

   private fun onCommandFinished(command: UploadFeedbackAttachmentCommand) {
      updateImageAttachment(command)
      attachmentsManager.onCommandFinished(command)
   }

   private fun updateImageAttachment(command: UploadFeedbackAttachmentCommand) {
      val updatedHolder = command.entityStateHolder
      view.updateAttachment(updatedHolder)
      attachmentsManager.update(updatedHolder)
   }

   private fun uploadImageAttachment(path: Uri) {
      attachmentsManager.send(path.toString())
   }
}
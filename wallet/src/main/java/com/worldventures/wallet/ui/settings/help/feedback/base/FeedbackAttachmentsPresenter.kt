package com.worldventures.wallet.ui.settings.help.feedback.base

import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.core.modules.picker.model.PhotoPickerModel

interface FeedbackAttachmentsPresenter {

   fun fetchAttachments()

   fun handleAttachedImages(chosenImages: List<PhotoPickerModel>)

   fun removeAttachment(holder: EntityStateHolder<FeedbackImageAttachment>)

   fun retryUploadingAttachment(attachmentHolder: EntityStateHolder<FeedbackImageAttachment>)
}
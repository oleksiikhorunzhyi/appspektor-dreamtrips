package com.worldventures.wallet.ui.settings.help.feeback.payment

import com.nhaarman.mockito_kotlin.mock
import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.FeedbackAttachmentsPresenterDelegate
import rx.Observable
import rx.lang.kotlin.PublishSubject
import rx.subjects.PublishSubject

class MockFeedbackAttachmentsDelegate(val maxAttachmentsCount: Int) : FeedbackAttachmentsPresenterDelegate {

   private val attachmentsList: List<EntityStateHolder<FeedbackImageAttachment>> = ArrayList()
   private val attachmentsPublishSubject: PublishSubject<EntityStateHolder<FeedbackImageAttachment>> = PublishSubject()
   private var hasFailedOrProgress: Boolean = false

   fun addFailedAttachments() {
      hasFailedOrProgress = true

      val item: FeedbackImageAttachment = mock()
      attachmentsPublishSubject.onNext(EntityStateHolder.create(item, EntityStateHolder.State.FAIL))
   }

   fun addProgressAttachments() {
      hasFailedOrProgress = true

      val item: FeedbackImageAttachment = mock()
      attachmentsPublishSubject.onNext(EntityStateHolder.create(item, EntityStateHolder.State.PROGRESS))
   }

   fun addSuccessAttachment() {
      val item: FeedbackImageAttachment = mock()
      attachmentsPublishSubject.onNext(EntityStateHolder.create(item, EntityStateHolder.State.DONE))
   }

   // Mock implementation
   override fun fetchAttachments() { /* nothing */
   }

   override fun handleAttachedImages(chosenImages: List<PhotoPickerModel>) { /* nothing */
   }

   override fun removeAttachment(holder: EntityStateHolder<FeedbackImageAttachment>) { /* nothing */
   }

   override val imagesAttachments: List<FeedbackImageAttachment>
      get() = attachmentsList.map { it.entity() }

   override fun retryUploadingAttachment(attachmentHolder: EntityStateHolder<FeedbackImageAttachment>) { /* nothing */
   }

   override val availableAttachmentsCount: Int
      get() = maxAttachmentsCount - attachmentsList.size

   override val hasFailedOrPendingAttachments: Boolean
      get() = hasFailedOrProgress

   override val attachmentsObservable: Observable<EntityStateHolder<FeedbackImageAttachment>>
      get() = attachmentsPublishSubject.asObservable()

   override fun findPosition(holder: EntityStateHolder<FeedbackImageAttachment>): Int = 0

   override fun clearAttachments() {}

   override fun init(view: BaseFeedbackScreen) { /* nothing */
   }

   override fun destroy() { /* nothing */
   }
}
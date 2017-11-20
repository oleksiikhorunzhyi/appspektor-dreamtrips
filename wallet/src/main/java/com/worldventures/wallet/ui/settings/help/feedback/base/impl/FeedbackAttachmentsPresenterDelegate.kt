package com.worldventures.wallet.ui.settings.help.feedback.base.impl

import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen
import com.worldventures.wallet.ui.settings.help.feedback.base.FeedbackAttachmentsPresenter
import rx.Observable

interface FeedbackAttachmentsPresenterDelegate : FeedbackAttachmentsPresenter {

   val imagesAttachments: List<FeedbackImageAttachment>

   val availableAttachmentsCount: Int

   val hasFailedOrPendingAttachments: Boolean

   val attachmentsObservable: Observable<EntityStateHolder<FeedbackImageAttachment>>

   fun init(view: BaseFeedbackScreen)

   fun destroy()

   fun findPosition(holder: EntityStateHolder<FeedbackImageAttachment>): Int

   fun clearAttachments()
}

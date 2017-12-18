package com.worldventures.wallet.ui.settings.help.feedback.base

import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.wallet.service.command.settings.help.SendWalletFeedbackCommand
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import io.techery.janet.operationsubscriber.view.OperationView

interface BaseFeedbackScreen : WalletScreen {

   fun changeActionSendMenuItemEnabled(enable: Boolean)

   fun removeAttachment(holder: EntityStateHolder<FeedbackImageAttachment>)

   fun changeAddPhotosButtonEnabled(enable: Boolean)

   fun setAttachments(attachments: List<EntityStateHolder<FeedbackImageAttachment>>)

   fun updateAttachment(updatedHolder: EntityStateHolder<FeedbackImageAttachment>)

   fun pickPhoto(count: Int)

   fun provideOperationSendFeedback(): OperationView<SendWalletFeedbackCommand<*>>
}

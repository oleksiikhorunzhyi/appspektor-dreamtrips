package com.worldventures.wallet.ui.settings.help.feedback.base

import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.wallet.ui.common.base.WalletPresenter

interface BaseSendFeedbackPresenter<S : BaseFeedbackScreen> : WalletPresenter<S>, FeedbackAttachmentsPresenter {

   fun goBack()

   fun chosenAttachments()

   fun openFullScreenPhoto(holder: EntityStateHolder<FeedbackImageAttachment>)

   fun sendFeedback()
}

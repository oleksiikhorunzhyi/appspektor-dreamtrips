package com.worldventures.wallet.ui.settings.help.feedback.regular.impl

import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor
import com.worldventures.wallet.service.command.settings.help.CustomerSupportFeedbackCommand
import com.worldventures.wallet.service.command.settings.help.SendWalletFeedbackCommand
import com.worldventures.wallet.service.command.settings.help.SmartCardFeedbackCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackPresenterImpl
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.FeedbackAttachmentsPresenterDelegate
import com.worldventures.wallet.ui.settings.help.feedback.regular.FeedbackType
import com.worldventures.wallet.ui.settings.help.feedback.regular.SendFeedbackPresenter
import com.worldventures.wallet.ui.settings.help.feedback.regular.SendFeedbackScreen

class SendFeedbackPresenterImpl(navigator: Navigator,
                                deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                attachmentDelegate: FeedbackAttachmentsPresenterDelegate,
                                walletSettingsInteractor: WalletSettingsInteractor)
   : BaseFeedbackPresenterImpl<SendFeedbackScreen>(navigator, deviceConnectionDelegate, attachmentDelegate, walletSettingsInteractor),
      SendFeedbackPresenter {

   override fun sendFeedback() {
      view!!.changeActionSendMenuItemEnabled(false)
//
      sendFeedbackCommand(
            if (view!!.feedbackType == FeedbackType.SmartCardFeedback)
               SmartCardFeedbackCommand(view!!.feedbackMessage, attachmentDelegate.imagesAttachments)
            else
               CustomerSupportFeedbackCommand(view!!.feedbackMessage, attachmentDelegate.imagesAttachments)
      )
   }

   override fun attachView(view: SendFeedbackScreen) {
      super.attachView(view)
      observeFormValidation()

      val feedbackType = view.feedbackType
      view.applyFeedbackType(feedbackType)
   }

   private fun observeFormValidation() {
      view!!.textFeedbackMessage
            .map { it.isNotBlank() }
            .startWith(false)
            .compose(view!!.bindUntilDetach())
            .subscribe { enable ->
               view!!.changeActionSendMenuItemEnabled(enable && !attachmentDelegate.hasFailedOrPendingAttachments)
            }
   }

   public override fun handleSuccessSentFeedback() {
      attachmentDelegate.clearAttachments()
      goBack()
   }

   override fun handleFailSentFeedback(command: SendWalletFeedbackCommand<*>, throwable: Throwable) {
      view!!.changeActionSendMenuItemEnabled(true)
   }

   override fun goBack() {
      attachmentDelegate.clearAttachments()
      navigator.goBack()
   }
}

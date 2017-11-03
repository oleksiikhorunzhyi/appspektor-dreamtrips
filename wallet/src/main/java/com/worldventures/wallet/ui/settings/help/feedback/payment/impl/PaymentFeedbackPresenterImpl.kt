package com.worldventures.wallet.ui.settings.help.feedback.payment.impl


import com.worldventures.core.model.EntityStateHolder
import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor
import com.worldventures.wallet.service.command.settings.help.PaymentFeedbackCommand
import com.worldventures.wallet.service.command.settings.help.SendWalletFeedbackCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackPresenterImpl
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.FeedbackAttachmentsPresenterDelegate
import com.worldventures.wallet.ui.settings.help.feedback.payment.PaymentFeedbackPresenter
import com.worldventures.wallet.ui.settings.help.feedback.payment.PaymentFeedbackScreen

import rx.Observable

class PaymentFeedbackPresenterImpl(navigator: Navigator, deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                   feedbackAttachmentsPresenterDelegate: FeedbackAttachmentsPresenterDelegate,
                                   walletSettingsInteractor: WalletSettingsInteractor) : BaseFeedbackPresenterImpl<PaymentFeedbackScreen>(navigator, deviceConnectionDelegate, feedbackAttachmentsPresenterDelegate, walletSettingsInteractor), PaymentFeedbackPresenter {

   private val paymentFeedbackConverter: PaymentFeedbackConverter = PaymentFeedbackConverter()

   override fun attachView(view: PaymentFeedbackScreen) {
      super.attachView(view)
      observeFormValidation()
      observeUpdateStateAttachments()
   }

   private fun observeUpdateStateAttachments() {
      attachmentDelegate.attachmentsObservable
            .compose(view!!.bindUntilDetach())
            .subscribe { view!!.changeAddPhotosButtonEnabled(attachmentDelegate.hasFailedOrPendingAttachments) }
   }

   override fun goBack() {
      navigator.goBack()
   }

   override fun showBackConfirmation() {
      view!!.showBackConfirmDialog()
   }

   private fun observeFormValidation() {
      Observable.combineLatest(
            view!!.observeMerchantName()
                  .map<Boolean> { it.isNotBlank() }
                  .startWith(false),
            attachmentDelegate.attachmentsObservable
                  .map { holder -> holder.state() != EntityStateHolder.State.PROGRESS }
                  .startWith(true)
      ) { isMerchantNameValid, isAttachmentsUploadFinished ->
         isMerchantNameValid && isAttachmentsUploadFinished && !attachmentDelegate.hasFailedOrPendingAttachments
      }
            .compose(view!!.bindUntilDetach())
            .subscribe { enable -> view!!.changeActionSendMenuItemEnabled(enable) }
   }

   override fun sendFeedback() {
      view!!.changeActionSendMenuItemEnabled(false)

      sendFeedbackCommand(PaymentFeedbackCommand(
            paymentFeedbackConverter.createFeedback(view!!.paymentFeedbackViewModel),
            attachmentDelegate.imagesAttachments))
   }

   override fun discardChanges() {
      attachmentDelegate.clearAttachments()
      view!!.discardViewModelChanges()
      goBack()
   }

   override fun handleSuccessSentFeedback() {
      discardChanges()
   }

   override fun handleFailSentFeedback(command: SendWalletFeedbackCommand<*>, throwable: Throwable) {
      view!!.changeActionSendMenuItemEnabled(true)
   }
}

package com.worldventures.wallet.ui.settings.help.feedback.base.impl

import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor
import com.worldventures.wallet.service.command.settings.help.SendWalletFeedbackCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen
import com.worldventures.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter
import com.worldventures.wallet.ui.settings.help.feedback.base.FeedbackAttachmentsPresenter
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers

abstract class BaseFeedbackPresenterImpl<S : BaseFeedbackScreen>(navigator: Navigator,
                                                                 deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                                                 protected val attachmentDelegate: FeedbackAttachmentsPresenterDelegate,
                                                                 private val settingsInteractor: WalletSettingsInteractor)
   : WalletPresenterImpl<S>(navigator, deviceConnectionDelegate), BaseSendFeedbackPresenter<S>,
      FeedbackAttachmentsPresenter by attachmentDelegate {

   override fun attachView(view: S) {
      super.attachView(view)
      attachmentDelegate.init(view)
   }

   override fun chosenAttachments() {
      view.pickPhoto(attachmentDelegate.availableAttachmentsCount)
   }

   override fun openFullScreenPhoto(holder: EntityStateHolder<FeedbackImageAttachment>) {
      navigator.goFeedBackImageAttachments(attachmentDelegate.findPosition(holder), attachmentDelegate.imagesAttachments)
   }

   protected abstract fun handleSuccessSentFeedback()

   protected fun sendFeedbackCommand(feedbackCommand: SendWalletFeedbackCommand<*>) {
      settingsInteractor.walletFeedbackPipe()
            .createObservable(feedbackCommand)
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationSendFeedback())
                  .onSuccess { _ -> handleSuccessSentFeedback() }
                  .onFail { command, throwable -> this.handleFailSentFeedback(command, throwable) }
                  .create()
            )
   }

   override fun detachView(retainInstance: Boolean) {
      super.detachView(retainInstance)
      attachmentDelegate.destroy()
   }

   protected abstract fun handleFailSentFeedback(command: SendWalletFeedbackCommand<*>, throwable: Throwable)
}

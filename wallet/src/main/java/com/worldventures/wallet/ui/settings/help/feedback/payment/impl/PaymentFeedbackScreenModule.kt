package com.worldventures.wallet.ui.settings.help.feedback.payment.impl

import com.worldventures.core.modules.infopages.service.CancelableFeedbackAttachmentsManager
import com.worldventures.core.modules.infopages.service.FeedbackInteractor
import com.worldventures.core.modules.picker.service.MediaPickerInteractor
import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.FeedbackAttachmentsPresenterDelegateImpl
import com.worldventures.wallet.ui.settings.help.feedback.payment.PaymentFeedbackPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(PaymentFeedbackScreenImpl::class), complete = false)
class PaymentFeedbackScreenModule {

   @Provides
   fun providePaymentFeedbackPresenter(navigator: Navigator,
                                       deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                       feedbackInteractor: FeedbackInteractor, walletSettingsInteractor: WalletSettingsInteractor,
                                       mediaPickerInteractor: MediaPickerInteractor): PaymentFeedbackPresenter {
      return PaymentFeedbackPresenterImpl(navigator, deviceConnectionDelegate,
            FeedbackAttachmentsPresenterDelegateImpl(mediaPickerInteractor, feedbackInteractor,
                  CancelableFeedbackAttachmentsManager(feedbackInteractor.uploadAttachmentPipe())),
            walletSettingsInteractor)
   }
}

package com.worldventures.wallet.ui.settings.help.feedback.regular.impl

import com.worldventures.core.modules.infopages.service.CancelableFeedbackAttachmentsManager
import com.worldventures.core.modules.infopages.service.FeedbackInteractor
import com.worldventures.core.modules.picker.service.MediaPickerInteractor
import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.FeedbackAttachmentsPresenterDelegateImpl
import com.worldventures.wallet.ui.settings.help.feedback.regular.SendFeedbackPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(SendFeedbackScreenImpl::class), complete = false)
class SendFeedbackScreenModule {

   @Provides
   fun providesSendFeedbackPresenter(navigator: Navigator,
                                     deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                     feedbackInteractor: FeedbackInteractor,
                                     walletSettingsInteractor: WalletSettingsInteractor,
                                     mediaPickerInteractor: MediaPickerInteractor): SendFeedbackPresenter {
      return SendFeedbackPresenterImpl(navigator, deviceConnectionDelegate,
            FeedbackAttachmentsPresenterDelegateImpl(mediaPickerInteractor, feedbackInteractor,
                  CancelableFeedbackAttachmentsManager(feedbackInteractor.uploadAttachmentPipe())),
            walletSettingsInteractor)
   }
}

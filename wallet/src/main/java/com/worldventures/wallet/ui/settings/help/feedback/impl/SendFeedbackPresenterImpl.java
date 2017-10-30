package com.worldventures.wallet.ui.settings.help.feedback.impl;


import com.worldventures.core.modules.infopages.service.FeedbackInteractor;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.wallet.service.command.settings.help.CustomerSupportFeedbackCommand;
import com.worldventures.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.wallet.service.command.settings.help.SmartCardFeedbackCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.help.feedback.FeedbackType;
import com.worldventures.wallet.ui.settings.help.feedback.SendFeedbackPresenter;
import com.worldventures.wallet.ui.settings.help.feedback.SendFeedbackScreen;
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackPresenterImpl;

public class SendFeedbackPresenterImpl extends BaseFeedbackPresenterImpl<SendFeedbackScreen> implements SendFeedbackPresenter {

   public SendFeedbackPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FeedbackInteractor feedbackInteractor,
         WalletSettingsInteractor walletSettingsInteractor, MediaPickerInteractor mediaPickerInteractor) {
      super(navigator, deviceConnectionDelegate, feedbackInteractor, walletSettingsInteractor,
            mediaPickerInteractor);
   }

   @Override
   public void attachView(SendFeedbackScreen view) {
      super.attachView(view);
      observeFormValidation();
      final FeedbackType feedbackType = getView().getFeedbackType();
      getView().applyFeedbackType(feedbackType);
   }

   private void observeFormValidation() {
      getView().getTextFeedbackMessage().map(this::validForm)
            .compose(getView().bindUntilDetach())
            .subscribe(enable -> getView().changeActionSendMenuItemEnabled(enable));
   }

   private boolean validForm(CharSequence textMessage) {
      final boolean messageIsEmpty = textMessage.toString().trim().isEmpty();
      return !messageIsEmpty && getAttachmentsManager().getFailedOrPendingAttachmentsCount() <= 0;
   }

   @Override
   public void sendFeedback(String text) {
      getView().changeActionSendMenuItemEnabled(false);

      sendFeedbackCommand(getView().getFeedbackType() == FeedbackType.SmartCardFeedback
            ? new SmartCardFeedbackCommand(text, getImagesAttachments())
            : new CustomerSupportFeedbackCommand(text, getImagesAttachments()));
   }

   @Override
   public void handleSuccessSentFeedback() {
      clearAttachments();
      goBack();
   }

   @Override
   protected void handleFailSentFeedback(SendWalletFeedbackCommand command, Throwable throwable) {
      getView().changeActionSendMenuItemEnabled(true);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}

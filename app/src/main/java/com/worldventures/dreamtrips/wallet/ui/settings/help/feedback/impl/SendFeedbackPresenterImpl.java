package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.impl;


import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.CustomerSupportFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SmartCardFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.FeedbackType;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.SendFeedbackPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.SendFeedbackScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackPresenterImpl;

public class SendFeedbackPresenterImpl extends BaseFeedbackPresenterImpl<SendFeedbackScreen> implements SendFeedbackPresenter {

   public SendFeedbackPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FeedbackInteractor feedbackInteractor,
         WalletSettingsInteractor walletSettingsInteractor, MediaInteractor mediaInteractor) {
      super(navigator, smartCardInteractor, networkService, feedbackInteractor, walletSettingsInteractor,
            mediaInteractor);
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
            .compose(bindView())
            .subscribe(enable -> getView().changeActionSendMenuItemEnabled(enable));
   }

   private boolean validForm(CharSequence textMessage) {
      final boolean messageIsEmpty = textMessage.toString().trim().isEmpty();
      return !messageIsEmpty && getAttachmentsManager().getFailedOrPendingAttachmentsCount() <= 0;
   }

   @Override
   public void sendFeedback(String text) {
      getView().changeActionSendMenuItemEnabled(false);

      sendFeedbackCommand(getView().getFeedbackType() == FeedbackType.SmartCardFeedback ?
            new SmartCardFeedbackCommand(text, getImagesAttachments()) :
            new CustomerSupportFeedbackCommand(text, getImagesAttachments()));
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

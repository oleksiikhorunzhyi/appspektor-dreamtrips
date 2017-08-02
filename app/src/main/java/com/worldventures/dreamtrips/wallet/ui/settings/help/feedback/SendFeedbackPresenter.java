package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.CustomerSupportFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SmartCardFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

import rx.Observable;

public class SendFeedbackPresenter extends BaseSendFeedbackPresenter<SendFeedbackPresenter.Screen> {

   @Inject Navigator navigator;

   public SendFeedbackPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeFormValidation();
   }

   private void observeFormValidation() {
      getView().getTextFeedbackMessage().map(this::validForm)
            .compose(bindView())
            .subscribe(enable -> getView().changeActionSendMenuItemEnabled(enable));
   }

   private boolean validForm(CharSequence textMessage) {
      final boolean messageIsEmpty = textMessage.toString().trim().isEmpty();
      return !messageIsEmpty && attachmentsManager.getFailedOrPendingAttachmentsCount() <= 0;
   }

   public void goBack() {
      navigator.goBack();
   }

   void sendFeedback(SendFeedbackPath.FeedbackType feedbackType, String text) {
      getView().changeActionSendMenuItemEnabled(false);

      sendFeedbackCommand(feedbackType == SendFeedbackPath.FeedbackType.SmartCardFeedback ?
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

   public interface Screen extends BaseSendFeedbackPresenter.Screen {

      Observable<CharSequence> getTextFeedbackMessage();
   }
}

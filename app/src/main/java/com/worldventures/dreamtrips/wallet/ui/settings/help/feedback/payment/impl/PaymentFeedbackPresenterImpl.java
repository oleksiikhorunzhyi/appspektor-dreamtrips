package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.impl;


import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.PaymentFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.PaymentFeedbackDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.PaymentFeedbackPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.PaymentFeedbackScreen;

import rx.Observable;

public class PaymentFeedbackPresenterImpl extends BaseFeedbackPresenterImpl<PaymentFeedbackScreen> implements PaymentFeedbackPresenter {

   private final PaymentFeedbackDelegate paymentFeedbackDelegate;

   public PaymentFeedbackPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FeedbackInteractor feedbackInteractor,
         WalletSettingsInteractor walletSettingsInteractor, MediaPickerInteractor mediaPickerInteractor) {
      super(navigator, deviceConnectionDelegate, feedbackInteractor, walletSettingsInteractor, mediaPickerInteractor);
      this.paymentFeedbackDelegate = new PaymentFeedbackDelegate();
   }

   @Override
   public void attachView(PaymentFeedbackScreen view) {
      super.attachView(view);
      observeFormValidation();
      observeUpdateStateAttachments();

      getView().changeActionSendMenuItemEnabled(false);
   }

   private void observeUpdateStateAttachments() {
      getAttachmentsManager().getAttachmentsObservable()
            .compose(getView().bindUntilDetach())
            .subscribe(holder -> {
               final int attachmentsCount = getAttachmentsManager().getAttachments().size();
               getView().changeAddPhotosButtonEnabled(attachmentsCount < MAX_PHOTOS_ATTACHMENT);
            });
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   private void observeFormValidation() {
      Observable.combineLatest(
            getView().observeMerchantName()
                  .map(this::validForm),
            getAttachmentsManager()
                  .getAttachmentsObservable()
                  .map(holder -> holder.state() != EntityStateHolder.State.PROGRESS)
                  .startWith(true),
            (isMerchantNameValid, isAttachmentsUploadFinished)
                  -> isMerchantNameValid && isAttachmentsUploadFinished)
            .compose(getView().bindUntilDetach())
            .subscribe(enable -> getView().changeActionSendMenuItemEnabled(enable));
   }

   private boolean validForm(CharSequence merchantName) {
      final boolean messageIsEmpty = merchantName.toString().trim().isEmpty();
      return !messageIsEmpty && getAttachmentsManager().getFailedOrPendingAttachmentsCount() <= 0;
   }

   @Override
   public void sendFeedback() {
      getView().changeActionSendMenuItemEnabled(false);

      sendFeedbackCommand(new PaymentFeedbackCommand(
            paymentFeedbackDelegate.createFeedback(getView().getPaymentFeedbackViewModel()),
            paymentFeedbackDelegate.getImagesAttachments(getAttachmentsManager())));
   }

   @Override
   public void handleBackOnDataChangedAction() {
      getView().showBackConfirmDialog();
   }

   @Override
   public void discardChanges() {
      clearAttachments();
      getView().discardViewModelChanges();
      goBack();
   }

   @Override
   protected void handleSuccessSentFeedback() {
      discardChanges();
   }

   @Override
   protected void handleFailSentFeedback(SendWalletFeedbackCommand command, Throwable throwable) {
      getView().changeActionSendMenuItemEnabled(true);
   }
}

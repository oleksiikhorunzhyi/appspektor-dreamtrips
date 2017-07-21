package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.impl;


import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.PaymentFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.impl.BaseFeedbackPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.PaymentFeedbackDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.PaymentFeedbackPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.PaymentFeedbackScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model.PaymentFeedbackViewModel;

import rx.functions.Action0;

public class PaymentFeedbackPresenterImpl extends BaseFeedbackPresenterImpl<PaymentFeedbackScreen> implements PaymentFeedbackPresenter {

   private PaymentFeedbackViewModel startViewModel = new PaymentFeedbackViewModel();
   private final PaymentFeedbackDelegate paymentFeedbackDelegate;

   public PaymentFeedbackPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FeedbackInteractor feedbackInteractor,
         WalletSettingsInteractor walletSettingsInteractor, MediaInteractor mediaInteractor, Router router) {
      super(navigator, smartCardInteractor, networkService, feedbackInteractor, walletSettingsInteractor, mediaInteractor, router);
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
            .compose(bindView())
            .subscribe(holder -> {
               final int attachmentsCount = getAttachmentsManager().getAttachments().size();
               getView().changeAddPhotosButtonEnabled(attachmentsCount < MAX_PHOTOS_ATTACHMENT);
               getView().changeActionSendMenuItemEnabled(holder.state() != EntityStateHolder.State.PROGRESS);
            });
   }

   @Override
   public void goBack() {
      handleDataChanges(this::back);
   }

   public void back() {
      getNavigator().goBack();
   }

   private void observeFormValidation() {
      getView().observeMerchantName()
            .map(this::validForm)
            .compose(bindView())
            .subscribe(enable -> getView().changeActionSendMenuItemEnabled(enable));
   }

   private boolean validForm(CharSequence merchantName) {
      final boolean messageIsEmpty = merchantName.toString().trim().isEmpty();
      return !messageIsEmpty && getAttachmentsManager().getFailedOrPendingAttachmentsCount() <= 0;
   }

   @Override
   public boolean isDataChanged() {
      return !startViewModel.equals(getView().getPaymentFeedbackViewModel());
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
   protected void handleSuccessSentFeedback() {
      clearAttachments();
      back();
   }

   @Override
   protected void handleFailSentFeedback(SendWalletFeedbackCommand command, Throwable throwable) {
      getView().changeActionSendMenuItemEnabled(true);
   }

   private void handleDataChanges(Action0 action) {
      //TODO : check view model properly
      if (isDataChanged()) {
         action.call();
      } else {
         handleBackOnDataChangedAction();
      }
   }
}

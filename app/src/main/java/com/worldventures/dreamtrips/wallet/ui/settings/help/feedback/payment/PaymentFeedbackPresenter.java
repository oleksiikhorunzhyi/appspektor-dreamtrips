package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.PaymentFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.BaseSendFeedbackPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model.PaymentFeedbackViewModel;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

public class PaymentFeedbackPresenter extends BaseSendFeedbackPresenter<PaymentFeedbackPresenter.Screen> {

   @Inject Navigator navigator;
   @Inject BackStackDelegate backStackDelegate;

   private PaymentFeedbackViewModel startViewModel = new PaymentFeedbackViewModel();
   private PaymentFeedbackDelegate paymentFeedbackDelegate;

   public PaymentFeedbackPresenter(Context context, Injector injector) {
      super(context, injector);
      paymentFeedbackDelegate = new PaymentFeedbackDelegate();
   }

   private BackStackDelegate.BackPressedListener systemBackPressedListener = () -> {
      handleDataChanges(v -> back());
      return true;
   };

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      backStackDelegate.addListener(systemBackPressedListener);
      observeFormValidation();
      observeUpdateStateAttachments();

      getView().changeActionSendMenuItemEnabled(false);
   }

   private void observeUpdateStateAttachments() {
      attachmentsManager.getAttachmentsObservable()
            .compose(bindView())
            .subscribe(holder -> {
               final int attachmentsCount = attachmentsManager.getAttachments().size();
               getView().changeAddPhotosButtonEnabled(attachmentsCount < MAX_PHOTOS_ATTACHMENT);
               getView().changeActionSendMenuItemEnabled(holder.state() != EntityStateHolder.State.PROGRESS);
            });
   }

   public void goBack() {
      handleDataChanges(v -> back());
   }

   public void back() {
      navigator.goBack();
   }

   private void observeFormValidation() {
      getView().observeMerchantName()
            .map(this::validForm)
            .compose(bindView())
            .subscribe(enable -> getView().changeActionSendMenuItemEnabled(enable));
   }

   private boolean validForm(CharSequence merchantName) {
      final boolean messageIsEmpty = merchantName.toString().trim().isEmpty();
      return !messageIsEmpty && attachmentsManager.getFailedOrPendingAttachmentsCount() <= 0;
   }

   void sendFeedback() {
      getView().changeActionSendMenuItemEnabled(false);

      sendFeedbackCommand(new PaymentFeedbackCommand(
            paymentFeedbackDelegate.createFeedback(getView().getPaymentFeedbackViewModel()),
            paymentFeedbackDelegate.getImagesAttachments(attachmentsManager)));
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

   private void handleDataChanges(Action1<Void> action1) {
      if (startViewModel.equals(getView().getPaymentFeedbackViewModel())) {
         action1.call(null);
      } else {
         getView().showBackConfirmDialog();
      }
   }

   @Override
   public void detachView(boolean retainInstance) {
      backStackDelegate.removeListener(systemBackPressedListener);
      super.detachView(retainInstance);
   }

   public interface Screen extends BaseSendFeedbackPresenter.Screen {

      Observable<CharSequence> observeMerchantName();

      PaymentFeedbackViewModel getPaymentFeedbackViewModel();

      void showBackConfirmDialog();
   }
}

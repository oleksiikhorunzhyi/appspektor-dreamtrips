package com.worldventures.wallet.ui.settings.help.feedback.payment;

import com.worldventures.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter;

public interface PaymentFeedbackPresenter extends BaseSendFeedbackPresenter<PaymentFeedbackScreen> {

   void sendFeedback();

   void handleBackOnDataChangedAction();

   void discardChanges();
}

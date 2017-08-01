package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment;

import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter;

public interface PaymentFeedbackPresenter extends BaseSendFeedbackPresenter<PaymentFeedbackScreen> {

   void sendFeedback();

   void handleBackOnDataChangedAction();
}

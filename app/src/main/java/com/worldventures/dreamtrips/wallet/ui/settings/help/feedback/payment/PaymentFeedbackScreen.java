package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment;

import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model.PaymentFeedbackViewModel;

import rx.Observable;

public interface PaymentFeedbackScreen extends BaseFeedbackScreen {

   Observable<CharSequence> observeMerchantName();

   PaymentFeedbackViewModel getPaymentFeedbackViewModel();

   void showBackConfirmDialog();

   void discardViewModelChanges();
}

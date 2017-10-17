package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.core.modules.infopages.service.FeedbackAttachmentsManager;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutableMerchant;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutablePaymentFeedback;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutablePaymentTerminal;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.Merchant;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.PaymentTerminal;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model.MerchantViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model.PaymentFeedbackViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model.PaymentTerminalViewModel;

import java.util.List;

public class PaymentFeedbackDelegate {

   public ImmutablePaymentFeedback.Builder createFeedback(PaymentFeedbackViewModel paymentFeedbackViewModel) {
      return ImmutablePaymentFeedback.builder()
            .paymentSucceeded(paymentFeedbackViewModel.getAttemptsView().isSuccessPayment())
            .countAttempts(paymentFeedbackViewModel.getAttemptsView().getCountOfAttempts())
            .merchant(fetchMerchant(paymentFeedbackViewModel.getMerchantView()))
            .paymentTerminal(fetchPaymentTerminalData(paymentFeedbackViewModel.getTerminalView()))
            .notes(paymentFeedbackViewModel.getInfoView().getNotes());
   }

   private PaymentTerminal fetchPaymentTerminalData(PaymentTerminalViewModel terminalView) {
      return ImmutablePaymentTerminal.builder()
            .nameAndModel(terminalView.getTerminalNameModel())
            .paymentType(terminalView.getPaymentType())
            .build();
   }

   private Merchant fetchMerchant(MerchantViewModel merchantView) {
      return ImmutableMerchant.builder()
            .address1(merchantView.getAddressLine1())
            .address2(merchantView.getAddressLine2())
            .name(merchantView.getMerchantName())
            .city(merchantView.getCity())
            .state(merchantView.getState())
            .zip(merchantView.getZip())
            .type(merchantView.getMerchantType())
            .build();
   }

   public List<FeedbackImageAttachment> getImagesAttachments(FeedbackAttachmentsManager attachmentsManager) {
      return Queryable.from(attachmentsManager.getAttachments()).map(EntityStateHolder::entity).toList();
   }
}

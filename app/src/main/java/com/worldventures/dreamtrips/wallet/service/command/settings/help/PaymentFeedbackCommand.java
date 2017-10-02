package com.worldventures.dreamtrips.wallet.service.command.settings.help;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.feedback.SendPaymentFeedbackHttpAction;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutablePaymentFeedback;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.PaymentFeedback;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class PaymentFeedbackCommand extends SendWalletFeedbackCommand<PaymentFeedback> {

   private final ImmutablePaymentFeedback.Builder paymentFeedbackBuilder;

   public PaymentFeedbackCommand(ImmutablePaymentFeedback.Builder paymentFeedbackBuilder, List<FeedbackImageAttachment> imageAttachments) {
      super("", imageAttachments);
      this.paymentFeedbackBuilder = paymentFeedbackBuilder;
   }

   @Override
   Observable<? extends AuthorizedHttpAction> provideHttpCommand(PaymentFeedback feedback) {
      return janet.createPipe(SendPaymentFeedbackHttpAction.class)
            .createObservableResult(new SendPaymentFeedbackHttpAction(feedback));
   }

   @Override
   PaymentFeedback provideFeedbackBody() {
      return paymentFeedbackBuilder
            .reasonId(0)
            .metadata(provideMetadata())
            .attachments(provideAttachments())
            .build();
   }
}

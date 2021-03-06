package com.worldventures.wallet.service.command.settings.help;

import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.feedback.SendFeedbackCustomerSupportHttpAction;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.ImmutableSmartCardFeedback;
import com.worldventures.dreamtrips.api.smart_card.feedback.model.SmartCardFeedback;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class CustomerSupportFeedbackCommand extends SendWalletFeedbackCommand<SmartCardFeedback> {

   public CustomerSupportFeedbackCommand(String description, List<FeedbackImageAttachment> imageAttachments) {
      super(description, imageAttachments);
   }

   @Override
   Observable<? extends AuthorizedHttpAction> provideHttpCommand(SmartCardFeedback feedback) {
      return janet.createPipe(SendFeedbackCustomerSupportHttpAction.class)
            .createObservableResult(new SendFeedbackCustomerSupportHttpAction(feedback));
   }

   @Override
   SmartCardFeedback provideFeedbackBody() {
      return ImmutableSmartCardFeedback.builder()
            .reasonId(0)
            .text(description)
            .metadata(provideMetadata())
            .smartCardMetadata(provideSmartCardMetadata())
            .attachments(provideAttachments()).build();
   }
}

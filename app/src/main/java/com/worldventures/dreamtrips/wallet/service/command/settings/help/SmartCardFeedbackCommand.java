package com.worldventures.dreamtrips.wallet.service.command.settings.help;


import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.feedback.model.Feedback;
import com.worldventures.dreamtrips.api.smart_card.feedback.SendFeedbackSmartCardHttpAction;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class SmartCardFeedbackCommand extends SendWalletFeedbackCommand {

   public SmartCardFeedbackCommand(String description, List<FeedbackImageAttachment> imageAttachments) {
      super(description, imageAttachments);
   }

   @Override
   Observable<? extends AuthorizedHttpAction> provideHttpCommand(Feedback feedback) {
      return janet.createPipe(SendFeedbackSmartCardHttpAction.class)
            .createObservableResult(new SendFeedbackSmartCardHttpAction(feedback));
   }

}
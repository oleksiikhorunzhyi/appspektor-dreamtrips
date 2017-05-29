package com.worldventures.dreamtrips.wallet.service.command.settings;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class SettingsHelpInteractor {

   private final ActionPipe<SendWalletFeedbackCommand> walletFeedbackPipe;
   private final ActionPipe<GetCustomerSupportContactCommand> customerSupportContactPipe;

   public SettingsHelpInteractor(SessionActionPipeCreator actionPipeCreator) {
      walletFeedbackPipe = actionPipeCreator.createPipe(SendWalletFeedbackCommand.class, Schedulers.io());
      customerSupportContactPipe = actionPipeCreator.createPipe(GetCustomerSupportContactCommand.class, Schedulers.io());
   }

   public ActionPipe<SendWalletFeedbackCommand> walletFeedbackPipe() {
      return walletFeedbackPipe;
   }

   public ActionPipe<GetCustomerSupportContactCommand> customerSupportContactPipe() {
      return customerSupportContactPipe;
   }

}
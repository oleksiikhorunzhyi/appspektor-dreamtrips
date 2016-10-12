package com.worldventures.dreamtrips.wallet.service.command.reset;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RemoveSmartCardDataCommand extends Command<Void> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;

   private final String smartCardId;

   public RemoveSmartCardDataCommand(String smartCardId) {
      this.smartCardId = smartCardId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      snappyRepository.deleteWalletCardList();
      snappyRepository.deleteSmartCardDetails(smartCardId);
      snappyRepository.deleteSmartCard(smartCardId);
      snappyRepository.deleteTermsAndConditions();
      callback.onSuccess(null);
   }

}

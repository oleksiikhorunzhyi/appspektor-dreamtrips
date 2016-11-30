package com.worldventures.dreamtrips.wallet.service.command.reset;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RemoveSmartCardDataCommand extends Command<Void> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;
   @Inject CardListStorage cardListStorage;

   private final String smartCardId;

   public RemoveSmartCardDataCommand(String smartCardId) {
      this.smartCardId = smartCardId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      snappyRepository.deleteActiveSmartCardId();
      snappyRepository.deleteWalletDefaultCardId();
      cardListStorage.deleteWalletCardList();
      snappyRepository.deleteSmartCardDetails(smartCardId);
      snappyRepository.deleteSmartCard(smartCardId);
      snappyRepository.deleteTermsAndConditions();
      snappyRepository.deleteFirmwareUpdateData();
      callback.onSuccess(null);
   }

}

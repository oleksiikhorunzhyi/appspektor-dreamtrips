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

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      snappyRepository.deleteWalletDefaultCardId();
      cardListStorage.deleteWalletCardList();
      snappyRepository.deleteSmartCardDetails();
      snappyRepository.deleteSmartCard();
      snappyRepository.deleteTermsAndConditions();
      callback.onSuccess(null);
   }

}

package com.worldventures.dreamtrips.wallet.service.command.reset;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RemoveSmartCardDataCommand extends Command<Void> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;
   @Inject RecordsStorage recordsStorage;
   @Inject LostCardRepository lostCardRepository;

   private final ResetOptions factoryResetOptions;

   public RemoveSmartCardDataCommand(ResetOptions factoryResetOptions) {
      this.factoryResetOptions = factoryResetOptions;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (factoryResetOptions.isWithPaymentCards()) deletePaymentsData();
      if (factoryResetOptions.isWithUserSmartCardData()) snappyRepository.deleteSmartCardUser();
      snappyRepository.deleteSmartCardFirmware();
      snappyRepository.deleteSmartCardDetails();
      snappyRepository.deleteSmartCard();
      snappyRepository.deleteTermsAndConditions();
      lostCardRepository.clear();
      callback.onSuccess(null);
   }

   private void deletePaymentsData() {
      recordsStorage.deleteAllRecords();
      recordsStorage.deleteDefaultRecordId();
   }
}

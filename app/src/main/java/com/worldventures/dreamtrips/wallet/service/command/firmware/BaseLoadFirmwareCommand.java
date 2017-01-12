package com.worldventures.dreamtrips.wallet.service.command.firmware;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.Subscription;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public abstract class BaseLoadFirmwareCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SnappyRepository snappyRepository;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      callback.onProgress(0);

      Observable<Integer> observable = provideProgress();
      Subscription subscription = observable.subscribe(callback::onProgress);

      loadFile()
            .subscribe(action -> {
               updateSmartCardUpdateStatus();
               subscription.unsubscribe();
               callback.onSuccess(null);
            }, callback::onFail);
   }

   private void updateSmartCardUpdateStatus() {
      SmartCard smartCard = snappyRepository.getSmartCard(snappyRepository.getActiveSmartCardId());
      if (smartCard != null && smartCard.firmwareVersion() != null) {
         SmartCardFirmware updatedSmartCardFirmware = updatedSmartCardFirmware(smartCard.firmwareVersion());
         snappyRepository.saveSmartCard(ImmutableSmartCard.copyOf(smartCard).withFirmwareVersion(updatedSmartCardFirmware));
      }
   }

   abstract Observable<Integer> provideProgress();

   abstract Observable<Void> loadFile();

   abstract SmartCardFirmware updatedSmartCardFirmware(SmartCardFirmware currentSmartCardFirmware);
}

package com.worldventures.dreamtrips.wallet.service.command.firmware;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.Observable;
import rx.Subscription;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

public abstract class BaseLoadFirmwareCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject FirmwareRepository firmwareRepository;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      callback.onProgress(0);

      Subscription subscription = provideProgress().subscribe(callback::onProgress);

      loadFile()
            .flatMap(aVoid -> updateFirmware())
            .subscribe(action -> {
               subscription.unsubscribe();
               callback.onSuccess(null);
            }, callback::onFail);
   }

   private Observable<Void> updateFirmware() {
      FirmwareUpdateData firmwareData = firmwareRepository.getFirmwareUpdateData();
      FirmwareUpdateData newFirmwareData = ImmutableFirmwareUpdateData.builder()
            .from(firmwareData)
            .currentFirmwareVersion(updatedSmartCardFirmware(firmwareData.currentFirmwareVersion()))
            .build();
      firmwareRepository.setFirmwareUpdateData(newFirmwareData);
      return Observable.just(null);
   }

   abstract Observable<Integer> provideProgress();

   abstract Observable<Void> loadFile();

   abstract SmartCardFirmware updatedSmartCardFirmware(SmartCardFirmware currentSmartCardFirmware);
}

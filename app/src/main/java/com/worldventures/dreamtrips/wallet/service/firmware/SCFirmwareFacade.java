package com.worldventures.dreamtrips.wallet.service.firmware;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;

import java.util.concurrent.atomic.AtomicBoolean;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class SCFirmwareFacade {

   private final FirmwareInteractor firmwareInteractor;
   private FirmwareRepository firmwareStorage;
   private final FirmwareDelegate firmwareDelegate;

   private final AtomicBoolean resetStarting = new AtomicBoolean(false);

   public SCFirmwareFacade(FirmwareInteractor firmwareInteractor,
         FirmwareDelegate firmwareDelegate,
         FirmwareRepository firmwareStorage) {

      this.firmwareDelegate = firmwareDelegate;
      this.firmwareInteractor = firmwareInteractor;
      this.firmwareStorage = firmwareStorage;

      observeFirmware();
   }

   public void prepareForUpdate() {
      firmwareDelegate.observeFirmwareInfo()
            .flatMap(firmwareUpdateData -> {
               resetStarting.set(true);
               return firmwareInteractor.prepareForUpdatePipe()
                     .createObservable(new PrepareForUpdateCommand(firmwareUpdateData));
            })
            .subscribe(new ActionStateSubscriber<PrepareForUpdateCommand>()
                  .onFail((command, throwable) -> resetStarting.set(false))
                  .onSuccess(command -> resetStarting.set(false))
            );
   }

   public ActionPipe<PrepareForUpdateCommand> prepareForUpdatePipe() {
      return firmwareInteractor.prepareForUpdatePipe();
   }

   // end force

   // fetch info
   public void fetchFirmwareInfo() {
      firmwareDelegate.fetchFirmwareInfo();
   }

   public Observable<FirmwareUpdateData> takeFirmwareInfo() {
      return firmwareDelegate.observeFirmwareInfo().take(1);
   }

   private void observeFirmware() {
      firmwareDelegate.fetchFirmwareInfoPipe()
            .observe()
            .filter(actionState -> actionState.status == ActionState.Status.SUCCESS)
            .map(actionState -> actionState.action.getResult())
            .subscribe(firmwareUpdateData -> firmwareStorage.setFirmwareUpdateData(firmwareUpdateData));
   }
}

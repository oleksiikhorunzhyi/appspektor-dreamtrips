package com.worldventures.dreamtrips.wallet.service.firmware;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;

import io.techery.janet.ActionPipe;
import rx.Observable;

public class SCFirmwareFacade {

   private final FirmwareInteractor firmwareInteractor;
   private final FirmwareRepository firmwareStorage;
   private final FirmwareDelegate firmwareDelegate;

   public SCFirmwareFacade(FirmwareInteractor firmwareInteractor,
         FirmwareDelegate firmwareDelegate,
         FirmwareRepository firmwareStorage) {

      this.firmwareDelegate = firmwareDelegate;
      this.firmwareInteractor = firmwareInteractor;
      this.firmwareStorage = firmwareStorage;
   }

   public void prepareForUpdate() {
      prepareForUpdatePipe().send(new PrepareForUpdateCommand());
   }

   public ActionPipe<PrepareForUpdateCommand> prepareForUpdatePipe() {
      return firmwareInteractor.prepareForUpdatePipe();
   }

   // fetch info
   public void fetchFirmwareInfo() {
      firmwareDelegate.fetchFirmwareInfo();
   }

   public Observable<FirmwareUpdateData> takeFirmwareInfo() {
      final FirmwareUpdateData firmwareUpdateData = firmwareStorage.getFirmwareUpdateData();
      if (firmwareUpdateData != null) {
         return Observable.fromCallable(() -> firmwareUpdateData);
      } else {
         return firmwareDelegate.observeFirmwareInfo(); // todo fetch from server
      }
   }
}

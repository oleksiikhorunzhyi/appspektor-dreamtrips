package com.worldventures.dreamtrips.wallet.service.firmware.command;

import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.UpgradeAppFirmwareAction;
import io.techery.janet.smartcard.event.UpgradeAppFirmwareProgressEvent;
import rx.Observable;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import java.io.File;

@CommandAction
public class LoadNordicFirmwareCommand extends BaseLoadFirmwareCommand {

   private final File firmwareFile;
   private final String firmwareVersion;
   private final boolean bootloaderFile;

   public LoadNordicFirmwareCommand(File firmwareFile, String firmwareVersion, boolean bootloaderFile) {
      this.firmwareFile = firmwareFile;
      this.firmwareVersion = firmwareVersion;
      this.bootloaderFile = bootloaderFile;
   }

   @Override
   Observable<Integer> provideProgress() {
      return janet.createPipe(UpgradeAppFirmwareProgressEvent.class)
            .observeSuccess()
            .map(event -> event.progress);
   }

   @Override
   Observable<Void> loadFile() {
      return janet.createPipe(UpgradeAppFirmwareAction.class)
            .createObservableResult(new UpgradeAppFirmwareAction(firmwareFile))
            .map(action -> null);
   }

   @Override
   SmartCardFirmware updatedSmartCardFirmware(SmartCardFirmware currentSmartCardFirmware) {
      return bootloaderFile ?
            ImmutableSmartCardFirmware.copyOf(currentSmartCardFirmware).withNrfBootloaderVersion(firmwareVersion) :
            ImmutableSmartCardFirmware.copyOf(currentSmartCardFirmware).withNordicAppVersion(firmwareVersion);
   }

}


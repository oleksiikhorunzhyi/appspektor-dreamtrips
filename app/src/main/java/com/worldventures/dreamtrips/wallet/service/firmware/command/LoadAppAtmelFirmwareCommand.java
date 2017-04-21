package com.worldventures.dreamtrips.wallet.service.firmware.command;

import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.UpgradeIntAtmelFirmwareAction;
import io.techery.janet.smartcard.event.UpgradeIntAtmelFirmwareProgressEvent;
import rx.Observable;

import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import java.io.File;

@CommandAction
public class LoadAppAtmelFirmwareCommand extends BaseLoadFirmwareCommand {

   private final File firmwareFile;
   private final String firmwareVersion;

   public LoadAppAtmelFirmwareCommand(File firmwareFile, String firmwareVersion) {
      this.firmwareFile = firmwareFile;
      this.firmwareVersion = firmwareVersion;
   }

   @Override
   Observable<Integer> provideProgress() {
      return janet.createPipe(UpgradeIntAtmelFirmwareProgressEvent.class)
            .observeSuccess()
            .map(event -> event.progress);
   }

   @Override
   Observable<Void> loadFile() {
      return janet.createPipe(UpgradeIntAtmelFirmwareAction.class)
            .createObservableResult(new UpgradeIntAtmelFirmwareAction(firmwareFile))
            .map(action -> null);
   }

   @Override
   SmartCardFirmware updatedSmartCardFirmware(SmartCardFirmware currentSmartCardFirmware) {
      return ImmutableSmartCardFirmware.copyOf(currentSmartCardFirmware).withInternalAtmelVersion(firmwareVersion);
   }

}

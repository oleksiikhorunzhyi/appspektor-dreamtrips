package com.worldventures.dreamtrips.wallet.service.command.firmware;

import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.UpgradeExtAtmelFirmwareAction;
import io.techery.janet.smartcard.event.UpgradeExtAtmelFirmwareProgressEvent;
import rx.Observable;

import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import java.io.File;

@CommandAction
public class LoadPuckAtmelFirmwareCommand extends BaseLoadFirmwareCommand {

   private final File firmwareFile;
   private final String firmwareVersion;

   public LoadPuckAtmelFirmwareCommand(File firmwareFile, String firmwareVersion) {
      this.firmwareFile = firmwareFile;
      this.firmwareVersion = firmwareVersion;
   }

   @Override
   Observable<Integer> provideProgress() {
      return janet.createPipe(UpgradeExtAtmelFirmwareProgressEvent.class)
            .observeSuccess()
            .map(event -> event.progress);
   }

   @Override
   Observable<Void> loadFile() {
      return janet.createPipe(UpgradeExtAtmelFirmwareAction.class)
            .createObservableResult(new UpgradeExtAtmelFirmwareAction(firmwareFile))
            .map(action -> (Void) null);
   }

   @Override
   SmartCardFirmware updatedSmartCardFirmware(SmartCardFirmware currentSmartCardFirmware) {
      return ImmutableSmartCardFirmware.copyOf(currentSmartCardFirmware).withExternalAtmelVersion(firmwareVersion);
   }
}

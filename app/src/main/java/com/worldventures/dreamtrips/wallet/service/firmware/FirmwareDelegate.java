package com.worldventures.dreamtrips.wallet.service.firmware;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;

@Deprecated
class FirmwareDelegate {

   private static final String SDK_FIRMWARE_VERSION_IF_NOT_PRESENT = "1.0.0";

   private SmartCardInteractor smartCardInteractor;
   private FirmwareInteractor firmwareInteractor;

   public FirmwareDelegate(SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor) {
      this.smartCardInteractor = smartCardInteractor;
      this.firmwareInteractor = firmwareInteractor;
   }

   void fetchFirmwareInfo() {
      Observable<SmartCard> observable = smartCardInteractor
            .activeSmartCardPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .filter(smartCard -> smartCard.connectionStatus() == CONNECTED)
            .filter(smartCard -> smartCard.firmwareVersion() != null)
            .take(1);
      observable.subscribe(this::fetchFirmwareInfo, throwable -> Timber.e(throwable, "Error while loading smartcard"));
   }

   private void fetchFirmwareInfo(SmartCard smartCard) {
      if (smartCard.firmwareVersion() == null) return;
      String sdkVersion = smartCard.sdkVersion().isEmpty() ? SDK_FIRMWARE_VERSION_IF_NOT_PRESENT : smartCard
            .sdkVersion();
      firmwareInteractor.firmwareInfoPipe().send(new FetchFirmwareInfoCommand(smartCard.smartCardId(), sdkVersion, smartCard.firmwareVersion()));
   }

   Observable<FirmwareUpdateData> observeFirmwareInfo() {
      return firmwareInteractor.firmwareInfoPipe()
            .observeSuccess()
            .map(Command::getResult);
   }

   ActionPipe<FetchFirmwareInfoCommand> fetchFirmwareInfoPipe() {
      return firmwareInteractor.firmwareInfoPipe();
   }
}

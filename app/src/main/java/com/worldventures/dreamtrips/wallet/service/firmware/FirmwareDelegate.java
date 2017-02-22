package com.worldventures.dreamtrips.wallet.service.firmware;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;

import io.techery.janet.Command;
import io.techery.janet.smartcard.util.SmartCardSDK;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus.CONNECTED;

@Deprecated
class FirmwareDelegate {

   private SmartCardInteractor smartCardInteractor;
   private FirmwareInteractor firmwareInteractor;

   public FirmwareDelegate(SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor) {
      this.smartCardInteractor = smartCardInteractor;
      this.firmwareInteractor = firmwareInteractor;
   }

   void fetchFirmwareInfo() {
      Observable.zip(
            smartCardInteractor.activeSmartCardPipe()
                  .createObservableResult(new ActiveSmartCardCommand()),
            smartCardInteractor.deviceStatePipe()
                  .createObservableResult(DeviceStateCommand.fetch()),
            (activeCommand, cardStateCommand) -> new Pair<>(activeCommand.getResult(), cardStateCommand.getResult()))
            .filter(pair -> pair.second.connectionStatus() == CONNECTED
                  && pair.first.cardStatus() == SmartCard.CardStatus.ACTIVE)
            .flatMap(pair -> fetchSmartCardFirmware())
            .subscribe(this::fetchServerFirmwareInfo,
                  throwable -> Timber.e(throwable, "Error while loading smartcard"));
   }

   private Observable<SmartCardFirmware> fetchSmartCardFirmware() {
      return smartCardInteractor
                  .smartCardFirmwarePipe()
                  .observeSuccessWithReplay()
                  .map(Command::getResult);
   }

   private void fetchServerFirmwareInfo(SmartCardFirmware smartCardFirmware) {
      if (smartCardFirmware == null) return;
      firmwareInteractor.firmwareInfoPipe().send(new FetchFirmwareInfoCommand(SmartCardSDK.getSDKVersion(), smartCardFirmware));
   }

   Observable<FirmwareUpdateData> observeFirmwareInfo() {
      return firmwareInteractor.firmwareInfoPipe()
            .observeSuccess()
            .map(Command::getResult);
   }

}

package com.worldventures.dreamtrips.wallet.delegate;


import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;

import io.techery.janet.Command;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;

public class FirmwareDelegate {

   private static final String SDK_FIRMWARE_VERSION_IF_NOT_PRESENT = "1.0.0";

   private SmartCardInteractor smartCardInteractor;
   private FirmwareInteractor firmwareInteractor;

   public FirmwareDelegate(SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor) {
      this.smartCardInteractor = smartCardInteractor;
      this.firmwareInteractor = firmwareInteractor;
   }

   public void fetchFirmwareInfo(@Nullable Observable.Transformer<SmartCard, SmartCard> viewLifecycle) {
      Observable<SmartCard> observable = smartCardInteractor
            .activeSmartCardPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .filter(smartCard -> smartCard.connectionStatus() == CONNECTED)
            .take(1);

      if (viewLifecycle != null) observable = observable.compose(viewLifecycle);

      observable.subscribe(smartCard -> {
         // in very first time user doesn't have installed firmware version, because this information are received only a from server.
         // so there was a decision to use a version of nordicApp firmware instead for performing request to check if there is a
         // new available update for a smart card in the server.
         String firmwareVersion = TextUtils.isEmpty(smartCard.firmwareVersion().firmwareVersion()) ?
               smartCard.firmwareVersion().nordicAppVersion() : smartCard.firmwareVersion().firmwareVersion();
         String sdkVersion = TextUtils.isEmpty(smartCard.sdkVersion()) ? SDK_FIRMWARE_VERSION_IF_NOT_PRESENT : smartCard.sdkVersion();
         firmwareInteractor.firmwareInfoPipe().send(new FetchFirmwareInfoCommand(sdkVersion, firmwareVersion));
      }, throwable -> Timber.e(throwable, "Error while loading smartcard"));
   }

   public Observable<FirmwareUpdateData> observeFirmwareInfo() {
      return firmwareInteractor.firmwareInfoPipe()
            .observeSuccess()
            .map(Command::getResult);
   }

}

package com.worldventures.dreamtrips.wallet.delegate;


import android.support.annotation.Nullable;

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

      observable.subscribe(smartCard -> firmwareInteractor.firmwareInfoPipe()
                  .send(new FetchFirmwareInfoCommand(smartCard.sdkVersion(), smartCard.firmWareVersion())),
            throwable -> Timber.e(throwable, "Error while loading smartcard"));
   }

   public Observable<FirmwareUpdateData> observeFirmwareInfo() {
      return firmwareInteractor.firmwareInfoPipe()
            .observeSuccess()
            .map(Command::getResult);
   }

}

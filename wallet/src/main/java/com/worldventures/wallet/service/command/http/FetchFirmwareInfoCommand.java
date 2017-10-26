package com.worldventures.wallet.service.command.http;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.api.smart_card.firmware.GetFirmwareHttpAction;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareResponse;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.domain.entity.ImmutableFirmwareUpdateData;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.firmware.FirmwareRepository;
import com.worldventures.wallet.service.firmware.command.FirmwareInfoCachedCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.util.SmartCardSDK;

@CommandAction
public class FetchFirmwareInfoCommand extends Command<FirmwareUpdateData> implements InjectableAction {

   @Inject Janet janet;
   @Inject WalletStorage walletStorage;
   @Inject FirmwareRepository firmwareRepository;
   @Inject FirmwareInteractor firmwareInteractor;

   private final SmartCardFirmware firmwareVersion;
   private final boolean skipCache;
   private final boolean markAsStarted;

   public FetchFirmwareInfoCommand(SmartCardFirmware firmwareVersion) {
      this(firmwareVersion, false, false);
   }

   public FetchFirmwareInfoCommand(SmartCardFirmware firmwareVersion, boolean skipCache, boolean markAsStarted) {
      this.firmwareVersion = firmwareVersion;
      this.skipCache = skipCache;
      this.markAsStarted = markAsStarted;
   }

   @Override
   protected void run(CommandCallback<FirmwareUpdateData> callback) throws Throwable {
      if (!skipCache && firmwareRepository.getFirmwareUpdateData() != null && firmwareRepository.getFirmwareUpdateData()
            .isStarted()) {
         callback.onSuccess(firmwareRepository.getFirmwareUpdateData());
      } else {
         janet.createPipe(GetFirmwareHttpAction.class)
               .createObservableResult(new GetFirmwareHttpAction(getFirmwareVersion(), SmartCardSDK.getSDKVersion()))
               .map(firmwareHttpAction -> createUpdateData(firmwareHttpAction.response()))
               .subscribe(firmwareUpdateData -> {
                  firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.save(firmwareUpdateData));
                  callback.onSuccess(firmwareUpdateData);
               }, callback::onFail);
      }
   }

   private FirmwareUpdateData createUpdateData(FirmwareResponse firmwareResponse) {
      SmartCard smartCard = walletStorage.getSmartCard();
      return ImmutableFirmwareUpdateData.builder()
            .smartCardId(smartCard.smartCardId())
            .currentFirmwareVersion(firmwareVersion)
            .firmwareInfo(firmwareResponse.firmwareInfo()) //todo: create converter and store data in domain model
            .updateAvailable(firmwareResponse.updateAvailable())
            .factoryResetRequired(firmwareResponse.factoryResetRequired())
            .updateCritical(firmwareResponse.updateCritical())
            .isStarted(markAsStarted)
            .build();
   }

   private String getFirmwareVersion() {
      // in very first time user doesn't have installed firmware version, because this information are received only a from server.
      // so there was a decision to use a version of nordicApp firmware instead for performing request to check if there is a
      // new available update for a smart card in the server.
      return firmwareVersion.firmwareBundleVersion() == null ? firmwareVersion.nordicAppVersion() : firmwareVersion.firmwareBundleVersion();
   }
}

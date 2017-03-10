package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.firmware.GetFirmwareHttpAction;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareResponse;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.util.SmartCardSDK;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class FetchFirmwareInfoCommand extends Command<FirmwareUpdateData> implements InjectableAction {

   @Inject MapperyContext mapperyContext;
   @Inject @Named(JANET_API_LIB) Janet janet;
   @Inject SnappyRepository snappyRepository;
   @Inject FirmwareRepository firmwareRepository;
   @Inject FirmwareInteractor firmwareInteractor;

   private final SmartCardFirmware firmwareVersion;

   public FetchFirmwareInfoCommand(SmartCardFirmware firmwareVersion) {
      this.firmwareVersion = firmwareVersion;
   }

   @Override
   protected void run(CommandCallback<FirmwareUpdateData> callback) throws Throwable {
      if (firmwareRepository.getFirmwareUpdateData() != null && firmwareRepository.getFirmwareUpdateData()
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
      SmartCard smartCard = snappyRepository.getSmartCard();
      return ImmutableFirmwareUpdateData.builder()
            .smartCardId(smartCard.smartCardId())
            .currentFirmwareVersion(firmwareVersion)
            .firmwareInfo(firmwareResponse.firmwareInfo()) //todo: create converter and store data in domain model
            .updateAvailable(firmwareResponse.updateAvailable())
            .factoryResetRequired(firmwareResponse.factoryResetRequired())
            .updateCritical(firmwareResponse.updateCritical())
            .build();
   }

   private String getFirmwareVersion() {
      // in very first time user doesn't have installed firmware version, because this information are received only a from server.
      // so there was a decision to use a version of nordicApp firmware instead for performing request to check if there is a
      // new available update for a smart card in the server.
      return firmwareVersion.firmwareBundleVersion() == null ? firmwareVersion.nordicAppVersion() : firmwareVersion.firmwareBundleVersion();
   }
}

package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.firmware.GetFirmwareHttpAction;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareResponse;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class FetchFirmwareInfoCommand extends Command<FirmwareUpdateData> implements InjectableAction {

   @Inject MapperyContext mapperyContext;
   @Inject @Named(JANET_API_LIB) Janet janet;

   private final String scId;
   private final String sdkVersion;
   private final SmartCardFirmware firmwareVersion;

   public FetchFirmwareInfoCommand(String scId, String sdkVersion, SmartCardFirmware firmwareVersion) {
      this.scId = scId;
      this.sdkVersion = sdkVersion;
      this.firmwareVersion = firmwareVersion;
   }

   @Override
   protected void run(CommandCallback<FirmwareUpdateData> callback) throws Throwable {
      janet.createPipe(GetFirmwareHttpAction.class)
            .createObservableResult(new GetFirmwareHttpAction(getFirmwareVersion(), sdkVersion))
            .map(firmwareHttpAction -> createUpdateData(firmwareHttpAction.response()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private FirmwareUpdateData createUpdateData(FirmwareResponse firmwareResponse) {
      return ImmutableFirmwareUpdateData.builder()
            .smartCardId(scId)
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
      return firmwareVersion.firmwareVersion() == null ? firmwareVersion.nordicAppVersion() : firmwareVersion.firmwareVersion();
   }
}

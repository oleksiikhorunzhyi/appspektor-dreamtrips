package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.firmware.GetFirmwareHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

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

   private final String sdkVersion;
   private final String firmwareVersion;

   public FetchFirmwareInfoCommand(String sdkVersion, String firmwareVersion) {
      this.sdkVersion = sdkVersion;
      this.firmwareVersion = firmwareVersion;
   }

   @Override
   protected void run(CommandCallback<FirmwareUpdateData> callback) throws Throwable {
      janet.createPipe(GetFirmwareHttpAction.class)
            .createObservableResult(new GetFirmwareHttpAction(firmwareVersion, sdkVersion))
            .map(it -> mapperyContext.convert(it.response(), FirmwareUpdateData.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

}

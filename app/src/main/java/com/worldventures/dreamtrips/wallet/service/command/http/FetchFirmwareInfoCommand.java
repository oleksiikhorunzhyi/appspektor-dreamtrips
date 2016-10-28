package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.firmware.GetFirmwareHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class FetchFirmwareInfoCommand extends Command<Firmware> implements InjectableAction {

   @Inject MapperyContext mapperyContext;
   @Inject @Named(JANET_API_LIB) Janet janet;

   private final String sdkVersion;
   private final String firmwareVersion;

   public FetchFirmwareInfoCommand(SmartCard smartCard) {
      this(smartCard.sdkVersion(), smartCard.firmWareVersion());
   }

   public FetchFirmwareInfoCommand(String sdkVersion, String firmwareVersion) {
      this.sdkVersion = sdkVersion;
      this.firmwareVersion = firmwareVersion;
   }

   @Override
   protected void run(CommandCallback<Firmware> callback) throws Throwable {
      fetchFirmwareInfo()
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Firmware> fetchFirmwareInfo() {
      return janet.createPipe(GetFirmwareHttpAction.class)
            .createObservableResult(new GetFirmwareHttpAction(firmwareVersion, sdkVersion))
            .map(it -> mapperyContext.convert(it.response(), Firmware.class));
   }
}

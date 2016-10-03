package com.worldventures.dreamtrips.wallet.service.command.http;


import com.worldventures.dreamtrips.api.smart_card.firmware.GetFirmwareHttpAction;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareResponse;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.ImmutableFirmwareInfo;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.ImmutableFirmwareResponse;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class FetchFirmwareInfoCommand extends Command<Firmware> implements InjectableAction {

   @Inject TemporaryStorage temporaryStorage;
   @Inject MapperyContext mapperyContext;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject @Named(JANET_API_LIB) Janet janet;

   @Override
   protected void run(CommandCallback<Firmware> callback) throws Throwable {

      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .flatMap(it -> janet.createPipe(GetFirmwareHttpAction.class)
                  .createObservableResult(new GetFirmwareHttpAction(it.getResult()
                        .sdkVersion(), appVersionNameBuilder.getReleaseSemanticVersionName())))
            .map(it -> mapperyContext.convert(temporaryStorage.newFirmwareIsAvailable() ? getMockInfo() : it.response(), Firmware.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private FirmwareResponse getMockInfo() {
      ImmutableFirmwareInfo firmwareInfo = ImmutableFirmwareInfo.builder()
            .isCompatible(temporaryStorage.firmwareIsCompatible())
            // test vary big description
            .releaseNotes("To make our SmartCard better for you, we have updates every 2 week. " +
                  "Every update of our smart card includes improvements for speed and reliability. " +
                  "As other new features become available, we’ll highlight those for you in the app. \n\n" +

                  "Repeat 2: To make our SmartCard better for you, we have updates every 2 week. " +
                  "Every update of our smart card includes improvements for speed and reliability. " +
                  "As other new features become available, we’ll highlight those for you in the app. \n\n" +

                  "Repeat 3: To make our SmartCard better for you, we have updates every 2 week. " +
                  "Every update of our smart card includes improvements for speed and reliability. " +
                  "As other new features become available, we’ll highlight those for you in the app. \n\n" +

                  "Repeat 4: To make our SmartCard better for you, we have updates every 2 week. " +
                  "Every update of our smart card includes improvements for speed and reliability. " +
                  "As other new features become available, we’ll highlight those for you in the app. \n\n" +


                  "Repeat 5: To make our SmartCard better for you, we have updates every 2 week. " +
                  "Every update of our smart card includes improvements for speed and reliability. " +
                  "As other new features become available, we’ll highlight those for you in the app. \n\n" +


                  "Repeat 6: To make our SmartCard better for you, we have updates every 2 week. " +
                  "Every update of our smart card includes improvements for speed and reliability. " +
                  "As other new features become available, we’ll highlight those for you in the app. \n\n" +

                  "Repeat 7: To make our SmartCard better for you, we have updates every 2 week. " +
                  "Every update of our smart card includes improvements for speed and reliability. " +
                  "As other new features become available, we’ll highlight those for you in the app."
            )
            .firmwareVersion("5.0.0")
            .firmwareName("5.0.0")
            .sdkVersion("1.0.0")
            .fileSize(5000)
            .id("magic id")
            .url("http://uweziegenhagen.de/wp-content/uploads/2012/06/BRD.pdf")
            .build();
      return ImmutableFirmwareResponse.builder().updateAvailable(true).firmwareInfo(firmwareInfo).build();
   }
}

package com.worldventures.dreamtrips.wallet.service.command.http;


import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareResponse;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareResponse;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class FetchFirmwareInfoCommand extends Command<Firmware> implements InjectableAction {

   @Inject TemporaryStorage temporaryStorage;
   @Inject MapperyContext mapperyContext;
   @Inject SmartCardInteractor smartCardInteractor;
   private final long versionCode;
   private final long appVersion;

   public FetchFirmwareInfoCommand() {
      this.versionCode = 0;//todo get smart card version from smartcard interactor
      this.appVersion = BuildConfig.VERSION_CODE;
   }

   @Override
   protected void run(CommandCallback<Firmware> callback) throws Throwable {
      Thread.sleep(1500); // for simulate server response
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .doOnNext(it -> {/*todo send serer request*/ })
            .map(it -> mapperyContext.convert(getMockInfo(), Firmware.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private FirmwareResponse getMockInfo() {
      if (temporaryStorage.newFirmwareIsAvailable()) {
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
               .versionName("5.0.0")
               .id("magic id")
               .url("http://uweziegenhagen.de/wp-content/uploads/2012/06/BRD.pdf")
               .build();
         return ImmutableFirmwareResponse.builder().updateAvailable(true).firmwareInfo(firmwareInfo).build();
      } else {
         return ImmutableFirmwareResponse.builder().updateAvailable(false).build();
      }
   }
}

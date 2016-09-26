package com.worldventures.dreamtrips.wallet.service.command.http;


import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchFirmwareInfoCommand extends Command<FirmwareInfo> implements InjectableAction {

   @Inject TemporaryStorage temporaryStorage;

   private final long versionCode;
   private final long appVersion;

   public FetchFirmwareInfoCommand() {
      this.versionCode = 0;//todo get smart card version from smartcard interactor
      this.appVersion = BuildConfig.VERSION_CODE;
   }

   @Override
   protected void run(CommandCallback<FirmwareInfo> callback) throws Throwable {
      Thread.sleep(1500); // for simulate server response
      callback.onSuccess(getMockInfo());
   }

   private FirmwareInfo getMockInfo() {
      if (temporaryStorage.newFirmwareIsAvailable()) {
         return ImmutableFirmwareInfo.builder()
               .isCompatible(temporaryStorage.firmwareIsCompatible())
               .byteSize(10000)
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
               .downloadUrl("http://uweziegenhagen.de/wp-content/uploads/2012/06/BRD.pdf")
               .build();
      } else {
         return ImmutableFirmwareInfo.builder()
               .isCompatible(false)
               .byteSize(0)
               .build();
      }
   }
}

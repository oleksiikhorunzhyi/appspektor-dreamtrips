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
      callback.onSuccess(getMockInfo());
   }

   private FirmwareInfo getMockInfo() {
      if (temporaryStorage.newFirmwareIsAvailable()) {
         return ImmutableFirmwareInfo.builder()
               .isCompatible(temporaryStorage.firmwareIsCompatible())
               .byteSize(10000)
               .releaseNotes("Amazing new update")
               .versionName("5.0.0")
               .build();
      } else {
         return ImmutableFirmwareInfo.builder()
               .isCompatible(false)
               .byteSize(0)
               .build();
      }
   }
}

package com.worldventures.dreamtrips.wallet.service.command.http;


import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareInfo;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchFirmwareInfoCommand extends Command<FirmwareInfo> {

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
      return ImmutableFirmwareInfo.builder()
            .isCompatible(true)
            .byteSize((long) 10000)
            .releaseNotes("Amazing new update")
            .versionName("5.0.0")
            .build();
   }
}

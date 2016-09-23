package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@Singleton
public class FirmwareInteractor {
   private final ActionPipe<FetchFirmwareInfoCommand> firmwareInfo;

   @Inject
   public FirmwareInteractor(@Named(JANET_WALLET) Janet janet) {
      firmwareInfo = janet.createPipe(FetchFirmwareInfoCommand.class, Schedulers.io());
   }

   public ActionPipe<FetchFirmwareInfoCommand> firmwareInfoPipe() {
      return firmwareInfo;
   }
}

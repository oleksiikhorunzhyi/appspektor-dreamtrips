package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.firmware.PreInstallationCheckCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class FirmwareInteractor {
   private final ActionPipe<FetchFirmwareInfoCommand> firmwareInfo;
   private final ActionPipe<PreInstallationCheckCommand> preInstallationCheckPipe;

   public FirmwareInteractor(Janet walletJanet) {
      firmwareInfo = walletJanet.createPipe(FetchFirmwareInfoCommand.class, Schedulers.io());
      preInstallationCheckPipe = walletJanet.createPipe(PreInstallationCheckCommand.class, Schedulers.io());
   }

   public ActionPipe<FetchFirmwareInfoCommand> firmwareInfoPipe() {
      return firmwareInfo;
   }

   public ActionPipe<PreInstallationCheckCommand> preInstallationCheckPipe() {
      return preInstallationCheckPipe;
   }
}

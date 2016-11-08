package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.firmware.FirmwareUpdateCacheCommand;
import com.worldventures.dreamtrips.wallet.service.command.firmware.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.command.firmware.PreInstallationCheckCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class FirmwareInteractor {

   private final ActionPipe<FetchFirmwareInfoCommand> firmwareInfo;
   private final ActionPipe<PreInstallationCheckCommand> preInstallationCheckPipe;
   private final ActionPipe<InstallFirmwareCommand> installFirmware;
   private final ActionPipe<FirmwareUpdateCacheCommand> firmwareCachePipe;

   public FirmwareInteractor(Janet walletJanet) {
      firmwareInfo = walletJanet.createPipe(FetchFirmwareInfoCommand.class, Schedulers.io());
      preInstallationCheckPipe = walletJanet.createPipe(PreInstallationCheckCommand.class, Schedulers.io());
      installFirmware = walletJanet.createPipe(InstallFirmwareCommand.class, Schedulers.io());
      firmwareCachePipe = walletJanet.createPipe(FirmwareUpdateCacheCommand.class, Schedulers.io());
   }

   public ActionPipe<FetchFirmwareInfoCommand> firmwareInfoPipe() {
      return firmwareInfo;
   }

   public ActionPipe<PreInstallationCheckCommand> preInstallationCheckPipe() {
      return preInstallationCheckPipe;
   }

   public ActionPipe<FirmwareUpdateCacheCommand> firmwareCachePipe() {
      return firmwareCachePipe;
   }

   public ActionPipe<InstallFirmwareCommand> installFirmwarePipe() {
      return installFirmware;
   }
}

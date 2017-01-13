package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.firmware.FirmwareClearFilesCommand;
import com.worldventures.dreamtrips.wallet.service.command.firmware.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.command.firmware.PreInstallationCheckCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FetchFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.smartcard.event.UpgradeAppFirmwareProgressEvent;
import rx.schedulers.Schedulers;

public class FirmwareInteractor {

   private final ActionPipe<FetchFirmwareInfoCommand> firmwareInfo;
   private final ActionPipe<PreInstallationCheckCommand> preInstallationCheckPipe;
   private final ActionPipe<InstallFirmwareCommand> installFirmware;
   private final ActionPipe<FirmwareClearFilesCommand> firmwareClearFilesPipe;
   private final ActionPipe<UpgradeAppFirmwareProgressEvent> upgradeAppFirmwareProgressEventActionPipe;
   private final ActionPipe<PrepareForUpdateCommand> prepareForUpdatePipe;
   private final ActionPipe<FetchFirmwareUpdateData> fetchFirmwareUpdateDataPipe;

   public FirmwareInteractor(Janet walletJanet) {
      firmwareInfo = walletJanet.createPipe(FetchFirmwareInfoCommand.class, Schedulers.io());
      preInstallationCheckPipe = walletJanet.createPipe(PreInstallationCheckCommand.class, Schedulers.io());
      installFirmware = walletJanet.createPipe(InstallFirmwareCommand.class, Schedulers.io());
      firmwareClearFilesPipe = walletJanet.createPipe(FirmwareClearFilesCommand.class, Schedulers.io());
      upgradeAppFirmwareProgressEventActionPipe = walletJanet.createPipe(UpgradeAppFirmwareProgressEvent.class, Schedulers
            .io());
      prepareForUpdatePipe = walletJanet.createPipe(PrepareForUpdateCommand.class, Schedulers.io());
      fetchFirmwareUpdateDataPipe = walletJanet.createPipe(FetchFirmwareUpdateData.class, Schedulers.io());
   }

   public ActionPipe<FetchFirmwareInfoCommand> firmwareInfoPipe() {
      return firmwareInfo;
   }

   public ActionPipe<PreInstallationCheckCommand> preInstallationCheckPipe() {
      return preInstallationCheckPipe;
   }

   public ActionPipe<InstallFirmwareCommand> installFirmwarePipe() {
      return installFirmware;
   }

   public ActionPipe<FirmwareClearFilesCommand> clearFirmwareFilesPipe() {
      return firmwareClearFilesPipe;
   }

   public ActionPipe<UpgradeAppFirmwareProgressEvent> upgradeAppFirmwareProgressEventActionPipe() {
      return upgradeAppFirmwareProgressEventActionPipe;
   }

   public ActionPipe<PrepareForUpdateCommand> prepareForUpdatePipe() {
      return prepareForUpdatePipe;
   }

   public ActionPipe<FetchFirmwareUpdateData> fetchFirmwareUpdateDataPipe() {
      return fetchFirmwareUpdateDataPipe;
   }
}

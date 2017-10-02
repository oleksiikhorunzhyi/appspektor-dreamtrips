package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.ConnectForFirmwareUpdate;
import com.worldventures.dreamtrips.wallet.service.firmware.command.DownloadFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FetchFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareClearFilesCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;

import java.util.concurrent.Executors;

import io.techery.janet.ActionPipe;
import io.techery.janet.smartcard.event.UpgradeAppFirmwareProgressEvent;
import rx.schedulers.Schedulers;

public class FirmwareInteractor {

   private final ActionPipe<FetchFirmwareInfoCommand> firmwareInfo;
   private final ActionPipe<InstallFirmwareCommand> installFirmware;
   private final ActionPipe<FirmwareClearFilesCommand> firmwareClearFilesPipe;
   private final ActionPipe<UpgradeAppFirmwareProgressEvent> upgradeAppFirmwareProgressEventPipe;
   private final ActionPipe<PrepareForUpdateCommand> prepareForUpdatePipe;
   private final ActionPipe<FetchFirmwareUpdateData> fetchFirmwareUpdateDataPipe;
   private final ActionPipe<ConnectForFirmwareUpdate> connectForFirmwareUpdatePipe;
   private final ActionPipe<DownloadFirmwareCommand> downloadFirmwarePipe;
   private final ActionPipe<FirmwareInfoCachedCommand> firmwareInfoPipe;
   private final ActionPipe<FetchFirmwareUpdateDataCommand> fetchFirmwareUpdateDataCommandActionPipe;

   public FirmwareInteractor(SessionActionPipeCreator pipeCreator) {
      firmwareInfo = pipeCreator.createPipe(FetchFirmwareInfoCommand.class, Schedulers.io());
      installFirmware = pipeCreator.createPipe(InstallFirmwareCommand.class, Schedulers.io());
      firmwareClearFilesPipe = pipeCreator.createPipe(FirmwareClearFilesCommand.class, Schedulers.io());
      upgradeAppFirmwareProgressEventPipe = pipeCreator.createPipe(UpgradeAppFirmwareProgressEvent.class, Schedulers.io());
      prepareForUpdatePipe = pipeCreator.createPipe(PrepareForUpdateCommand.class, Schedulers.io());
      fetchFirmwareUpdateDataPipe = pipeCreator.createPipe(FetchFirmwareUpdateData.class, Schedulers.io());
      connectForFirmwareUpdatePipe = pipeCreator.createPipe(ConnectForFirmwareUpdate.class, Schedulers.io());
      downloadFirmwarePipe = pipeCreator.createPipe(DownloadFirmwareCommand.class, Schedulers.io());
      firmwareInfoPipe = pipeCreator.createPipe(FirmwareInfoCachedCommand.class, Schedulers.from(Executors.newSingleThreadExecutor()));
      fetchFirmwareUpdateDataCommandActionPipe = pipeCreator.createPipe(FetchFirmwareUpdateDataCommand.class, Schedulers
            .io());
   }

   public ActionPipe<FetchFirmwareInfoCommand> fetchFirmwareInfoPipe() {
      return firmwareInfo;
   }

   public ActionPipe<FirmwareInfoCachedCommand> firmwareInfoCachedPipe() {
      return firmwareInfoPipe;
   }

   public ActionPipe<InstallFirmwareCommand> installFirmwarePipe() {
      return installFirmware;
   }

   public ActionPipe<FirmwareClearFilesCommand> clearFirmwareFilesPipe() {
      return firmwareClearFilesPipe;
   }

   public ActionPipe<UpgradeAppFirmwareProgressEvent> upgradeAppFirmwareProgressEventActionPipe() {
      return upgradeAppFirmwareProgressEventPipe;
   }

   public ActionPipe<PrepareForUpdateCommand> prepareForUpdatePipe() {
      return prepareForUpdatePipe;
   }

   public ActionPipe<FetchFirmwareUpdateData> fetchFirmwareUpdateDataPipe() {
      return fetchFirmwareUpdateDataPipe;
   }

   public ActionPipe<ConnectForFirmwareUpdate> connectForFirmwareUpdatePipe() {
      return connectForFirmwareUpdatePipe;
   }

   public ActionPipe<DownloadFirmwareCommand> downloadFirmwarePipe() {
      return downloadFirmwarePipe;
   }

   public ActionPipe<FetchFirmwareUpdateDataCommand> fetchFirmwareUpdateDataCommandActionPipe() {
      return fetchFirmwareUpdateDataCommandActionPipe;
   }

}

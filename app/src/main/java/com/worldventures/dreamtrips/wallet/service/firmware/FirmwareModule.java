package com.worldventures.dreamtrips.wallet.service.firmware;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.service.firmware.command.ConnectForFirmwareUpdate;
import com.worldventures.dreamtrips.wallet.service.firmware.command.DownloadFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FetchFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.command.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.LoadAppAtmelFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.LoadFirmwareFilesCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.LoadNordicFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.LoadPuckAtmelFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.UnzipFirmwareCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            InstallFirmwareCommand.class,
            LoadPuckAtmelFirmwareCommand.class,
            LoadAppAtmelFirmwareCommand.class,
            LoadNordicFirmwareCommand.class,
            LoadFirmwareFilesCommand.class,
            UnzipFirmwareCommand.class,
            PrepareForUpdateCommand.class,
            FetchFirmwareUpdateData.class,
            ConnectForFirmwareUpdate.class,
            DownloadFirmwareCommand.class
      },
      library = true, complete = false)
public class FirmwareModule {

   @Provides
   @Singleton
   FirmwareRepository firmwareRepository(SnappyRepository snappyRepository) {
      return new DiskFirmwareRepository(snappyRepository);
   }
}

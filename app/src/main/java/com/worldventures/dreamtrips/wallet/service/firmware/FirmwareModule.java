package com.worldventures.dreamtrips.wallet.service.firmware;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FetchFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            PrepareForUpdateCommand.class,
            FetchFirmwareUpdateData.class
      },
      library = true, complete = false)
public class FirmwareModule {

   @Provides
   @Singleton
   FirmwareRepository firmwareRepository(SnappyRepository snappyRepository) {
      return new DiskFirmwareRepository(snappyRepository);
   }

   @Provides
   FirmwareDelegate provideFirmwareDelegate(SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor) {
      return new FirmwareDelegate(smartCardInteractor, firmwareInteractor);
   }

   @Singleton
   @Provides
   SCFirmwareFacade firmwareFacade(FirmwareInteractor firmwareInteractor, FirmwareDelegate firmwareDelegate, FirmwareRepository firmwareRepository) {
      return new SCFirmwareFacade(firmwareInteractor, firmwareDelegate, firmwareRepository);
   }
}

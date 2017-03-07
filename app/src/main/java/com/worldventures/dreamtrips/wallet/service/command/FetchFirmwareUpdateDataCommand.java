package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchFirmwareUpdateDataCommand extends Command<FirmwareUpdateData> implements InjectableAction {

   @Inject FirmwareRepository firmwareRepository;

   @Override
   protected void run(CommandCallback<FirmwareUpdateData> callback) throws Throwable {
      if (firmwareRepository.getFirmwareUpdateData() != null && firmwareRepository.getFirmwareUpdateData()
            .isStarted()) {
         callback.onSuccess(firmwareRepository.getFirmwareUpdateData());
      } else {
         callback.onFail(new NullPointerException());
      }
   }
}

package com.worldventures.dreamtrips.wallet.service.firmware.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareUpdateType;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class PrepareForUpdateCommand extends Command<FirmwareUpdateType> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject FirmwareRepository firmwareRepository;

   @Override
   protected void run(CommandCallback<FirmwareUpdateType> callback) throws Throwable {
      final FirmwareUpdateData updateData = ImmutableFirmwareUpdateData.builder()
            .from(firmwareRepository.getFirmwareUpdateData())
            .isStarted(true)
            .build();
      firmwareRepository.setFirmwareUpdateData(updateData);

      if (!updateData.factoryResetRequired()) {
         callback.onSuccess(FirmwareUpdateType.NORMAL);
      } else {
         janet.createPipe(FactoryResetCommand.class)
               .createObservableResult(new FactoryResetCommand(ResetOptions.builder().build()))
               .subscribe(command -> callback.onSuccess(FirmwareUpdateType.CRITICAL), callback::onFail);
      }
   }


}

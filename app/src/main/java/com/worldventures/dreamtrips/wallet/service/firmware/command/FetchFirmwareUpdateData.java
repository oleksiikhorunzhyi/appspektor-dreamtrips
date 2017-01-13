package com.worldventures.dreamtrips.wallet.service.firmware.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchFirmwareUpdateData extends Command<FetchFirmwareUpdateData.Result> implements InjectableAction {

   @Inject FirmwareRepository firmwareRepository;

   @Override
   protected void run(CommandCallback<Result> callback) throws Throwable {
      final FirmwareUpdateData firmwareUpdateData = firmwareRepository.getFirmwareUpdateData();
      callback.onSuccess(ImmutableResult.builder()
            .hasUpdate(firmwareUpdateData != null)
            .firmwareUpdateData(firmwareUpdateData)
            .build());
   }

   @Value.Immutable
   public interface Result {

      boolean hasUpdate();

      @Nullable
      FirmwareUpdateData firmwareUpdateData();
   }
}

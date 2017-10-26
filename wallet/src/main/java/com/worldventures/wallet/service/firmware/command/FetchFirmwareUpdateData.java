package com.worldventures.wallet.service.firmware.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.service.firmware.FirmwareRepository;

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
            .isForceUpdateStarted(firmwareUpdateData != null && firmwareUpdateData.isStarted() && firmwareUpdateData.factoryResetRequired())
            .firmwareUpdateData(firmwareUpdateData)
            .build());
   }

   @Value.Immutable
   public static abstract class Result {

      @Deprecated
      @Value.Derived
      public boolean hasUpdate() {
         return isForceUpdateStarted();
      }

      public abstract boolean isForceUpdateStarted();

      @Nullable
      public abstract FirmwareUpdateData firmwareUpdateData();
   }
}

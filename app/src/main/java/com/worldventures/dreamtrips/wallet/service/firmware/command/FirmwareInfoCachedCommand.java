package com.worldventures.dreamtrips.wallet.service.firmware.command;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.command.CachedValueCommand;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class FirmwareInfoCachedCommand extends CachedValueCommand<FirmwareUpdateData> {

   private FirmwareInfoCachedCommand(Func1<FirmwareUpdateData, FirmwareUpdateData> operationFunc) {
      super(operationFunc);
   }

   public static FirmwareInfoCachedCommand fetch() {
      return new FirmwareInfoCachedCommand(info -> {
         if (info == null) {
            throw new NullPointerException("Firmware info is not exist");
         }
         return info;
      });
   }

   public static FirmwareInfoCachedCommand save(FirmwareUpdateData firmwareUpdateData) {
      return new FirmwareInfoCachedCommand(data -> firmwareUpdateData);
   }
}

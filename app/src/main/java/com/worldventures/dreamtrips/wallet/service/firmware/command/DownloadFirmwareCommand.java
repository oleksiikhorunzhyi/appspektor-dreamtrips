package com.worldventures.dreamtrips.wallet.service.firmware.command;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile;

@CommandAction
public class DownloadFirmwareCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject FirmwareRepository firmwareRepository;

   @Inject @ForApplication Context appContext; // todo: remove from command

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {

      final FirmwareUpdateData firmwareUpdateData = firmwareRepository.getFirmwareUpdateData();
      final FirmwareInfo firmwareInfo = firmwareUpdateData.firmwareInfo();
      if (firmwareInfo == null) throw new IllegalStateException("Firmware is not available");

      janet.createPipe(DownloadFileCommand.class)
            .createObservableResult(new DownloadFileCommand(getAppropriateFirmwareFile(appContext), firmwareInfo.url()))
            .map(command -> ImmutableFirmwareUpdateData.builder()
                  .from(firmwareUpdateData)
                  .firmwareFile(command.getResult())
                  .build())
            .subscribe(result -> {
               firmwareRepository.setFirmwareUpdateData(result);
               callback.onSuccess(null);
            }, callback::onFail);
   }
}

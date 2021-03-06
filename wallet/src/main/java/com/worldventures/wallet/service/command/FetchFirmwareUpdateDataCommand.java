package com.worldventures.wallet.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.wallet.service.firmware.FirmwareRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class FetchFirmwareUpdateDataCommand extends Command<FirmwareUpdateData> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject FirmwareRepository firmwareRepository;

   @Override
   protected void run(CommandCallback<FirmwareUpdateData> callback) throws Throwable {
      final FirmwareUpdateData cachedFirmwareUpdateData = firmwareRepository.getFirmwareUpdateData();

      smartCardInteractor.smartCardFirmwarePipe()
            .createObservableResult(SmartCardFirmwareCommand.Companion.fetch())
            .map(Command::getResult)
            .flatMap(smartCardFirmware -> {
               if (!cachedFirmwareUpdateData.getFirmwareInfo().isCompatible()) {
                  return firmwareInteractor.fetchFirmwareInfoPipe()
                        .createObservableResult(new FetchFirmwareInfoCommand(smartCardFirmware, true, true))
                        .map(FetchFirmwareInfoCommand::getResult);
               } else {
                  return Observable.just(cachedFirmwareUpdateData);
               }
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }
}

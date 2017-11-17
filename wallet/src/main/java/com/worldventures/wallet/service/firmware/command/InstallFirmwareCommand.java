package com.worldventures.wallet.service.firmware.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.wallet.service.firmware.FirmwareRepository;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ConnectionType;
import io.techery.janet.smartcard.model.ImmutableConnectionParams;
import rx.Observable;
import rx.Subscription;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class InstallFirmwareCommand extends Command<FirmwareUpdateData> implements InjectableAction {

   public static final int INSTALL_FIRMWARE_TOTAL_STEPS = 4;

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject FirmwareRepository firmwareRepository;
   @Inject SmartCardInteractor smartCardInteractor;

   private LoadFirmwareFilesCommand loadFirmwareFilesCommand;
   private ActionPipe<LoadFirmwareFilesCommand> loadFirmwareFilesCommandActionPipe;

   @Override
   protected void run(CommandCallback<FirmwareUpdateData> callback) throws Throwable {
      callback.onProgress(0);
      loadFirmwareFilesCommandActionPipe = janet.createPipe(LoadFirmwareFilesCommand.class);
      if (firmwareRepository.getFirmwareUpdateData() == null) {
         throw new IllegalStateException("FirmwareUpdateData does not exist");
      }
      prepareCardAndInstallFirmware(callback)
            .flatMap(aVoid -> addBundleVersion())
            .flatMap(aVoid -> clearFirmwareUpdateCache())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> prepareCardAndInstallFirmware(CommandCallback callback) {
      final FirmwareUpdateData firmwareUpdateData = firmwareRepository.getFirmwareUpdateData();
      return connectSmartCard(firmwareUpdateData.getSmartCardId())
            .flatMap(connectionType -> installFirmware(firmwareUpdateData, connectionType, callback));
   }

   private Observable<ConnectionType> connectSmartCard(String scId) {
      return janet.createPipe(ConnectAction.class)
            .createObservableResult(new ConnectAction(ImmutableConnectionParams.of(Long.parseLong(scId))))
            .map(connectAction -> connectAction.type)
            // hotfix for first Disconnect event from smart card
            .retry((count, throwable) -> count < 2);
   }

   private Observable<Void> installFirmware(FirmwareUpdateData firmwareUpdateData, ConnectionType connectionType, CommandCallback callback) {
      final SmartCardFirmware firmwareVersion = firmwareUpdateData.getCurrentFirmwareVersion();
      loadFirmwareFilesCommand = new LoadFirmwareFilesCommand(
            firmwareVersion,
            firmwareUpdateData.getFirmwareInfo().firmwareVersions(),
            firmwareUpdateData.getFirmwareFile(),
            connectionType == ConnectionType.DFU);

      Subscription subscription = loadFirmwareFilesCommandActionPipe.observe()
            .filter(actionState -> actionState.status == ActionState.Status.PROGRESS)
            .subscribe(actionState -> callback.onProgress(actionState.progress));

      return janet.createPipe(LoadFirmwareFilesCommand.class)
            .createObservableResult(loadFirmwareFilesCommand)
            .doOnCompleted(subscription::unsubscribe)
            .map(it -> (Void) null);
   }

   private Observable<FirmwareUpdateData> clearFirmwareUpdateCache() {
      firmwareInteractor.firmwareInfoCachedPipe().clearReplays();
      final FirmwareUpdateData data = firmwareRepository.getFirmwareUpdateData();
      firmwareRepository.clear();
      return firmwareInteractor.clearFirmwareFilesPipe()
            .createObservableResult(new FirmwareClearFilesCommand(data.getFirmwareFile().getParent()))
            .map(command -> data);
   }

   private Observable<Void> addBundleVersion() {
      final FirmwareUpdateData data = firmwareRepository.getFirmwareUpdateData();
      return smartCardInteractor.smartCardFirmwarePipe()
            .createObservableResult(SmartCardFirmwareCommand.Companion.bundleVersion(data.getFirmwareInfo().firmwareVersion()))
            .onErrorReturn(throwable -> null)
            .map(command -> null);
   }

   public int getCurrentStep() {
      if (loadFirmwareFilesCommand == null) {
         return 0;
      } else {
         return loadFirmwareFilesCommand.getCurrentStep();
      }
   }
}

package com.worldventures.dreamtrips.wallet.service.command.firmware;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import rx.Observable;
import rx.Subscription;

import static com.worldventures.dreamtrips.core.janet.CommandActionBaseHelper.ActionCommandSubscriber.wrap;
import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.DFU;
import static rx.Observable.error;
import static rx.Observable.just;

@CommandAction
public class InstallFirmwareCommand extends Command implements InjectableAction {

   public static final int INSTALL_FIRMWARE_TOTAL_STEPS = 4;

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject TemporaryStorage temporaryStorage;
   @Inject SnappyRepository snappyRepository;

   private final FirmwareUpdateData firmwareUpdateData;
   private final SmartCard smartCard;
   private LoadFirmwareFilesCommand loadFirmwareFilesCommand;
   private ActionPipe<LoadFirmwareFilesCommand> loadFirmwareFilesCommandActionPipe;

   public InstallFirmwareCommand(SmartCard smartCard, FirmwareUpdateData firmwareUpdateData) {
      this.firmwareUpdateData = firmwareUpdateData;
      this.smartCard = smartCard;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      loadFirmwareFilesCommandActionPipe = janet.createPipe(LoadFirmwareFilesCommand.class);

      cacheFirmwareUpdateData()
            .flatMap(aVoid -> smartCard == null ? activeSmartCard() : Observable.just(smartCard)
                  .flatMap(sc -> prepareCardAndInstallFirmware(sc, callback))
                  .flatMap(sc -> saveNewFirmwareVersion(sc))
            )
            .flatMap(aVoid -> clearFirmwareUpdateCache())
            .flatMap(aVoid -> clearFirmwareFilesCache())
            .subscribe(wrap(callback));
   }

   private Observable<SmartCard> prepareCardAndInstallFirmware(SmartCard smartCard, CommandCallback callback) {
      return prepareSmartCard(smartCard)
            .flatMap(it -> {
               if (it.connectionStatus() == CONNECTED) return enableLockUnlockDevice(false);
               else if (it.connectionStatus() == DFU) return just(it);
               else return error(new IllegalStateException("Can't connect to card on firmwareUpdateData upgrade"));
            })
            .map(it -> firmwareUpdateData.firmwareFile())
            .flatMap(file -> installFirmware(file, smartCard, callback))
            .doOnNext(aVoid -> enableLockUnlockDevice(true))
            .map(aVoid -> smartCard);
   }

   private Observable<SmartCard> prepareSmartCard(SmartCard smartCard) {
      return smartCard.connectionStatus() == CONNECTED
            || smartCard.connectionStatus() == DFU ? just(smartCard) : connectCard(smartCard);
   }

   private Observable<Void> enableLockUnlockDevice(boolean enable) {
      return janet.createPipe(EnableLockUnlockDeviceAction.class)
            .createObservableResult(new EnableLockUnlockDeviceAction(enable))
            .onErrorResumeNext(Observable.just(null))
            .map(action -> (Void) null);
   }

   private Observable<Void> installFirmware(File file, SmartCard smartCard, CommandCallback callback) {
      loadFirmwareFilesCommand = new LoadFirmwareFilesCommand(file, smartCard.firmwareVersion(), firmwareUpdateData.firmwareInfo()
            .firmwareVersions(),
            smartCard.connectionStatus() == DFU);
      Subscription subscription = loadFirmwareFilesCommandActionPipe.observe()
            .filter(actionState -> actionState.status == ActionState.Status.PROGRESS)
            .subscribe(actionState -> callback.onProgress(actionState.progress));

      return janet.createPipe(LoadFirmwareFilesCommand.class)
            .createObservableResult(loadFirmwareFilesCommand)
            .doOnCompleted(subscription::unsubscribe)
            .flatMap(action ->//todo remove it when temporary storage will be useless
                  temporaryStorage.failInstall() ? error(new RuntimeException()) : Observable.just(action))
            .map(it -> (Void) null);
   }

   private Observable saveNewFirmwareVersion(SmartCard smartCard) {
      return Observable.create(subscriber -> {
         final SmartCardFirmware firmware =
               ImmutableSmartCardFirmware.builder()
                     .from(smartCard.firmwareVersion())
                     .firmwareVersion(firmwareUpdateData.firmwareInfo().firmwareVersion())
                     .build();

         snappyRepository.saveSmartCard(ImmutableSmartCard.builder()
               .from(smartCard)
               .sdkVersion(firmwareUpdateData.firmwareInfo().sdkVersion())
               .firmwareVersion(firmware)
               .build());
         //
         if (subscriber.isUnsubscribed()) return;
         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }

   private Observable cacheFirmwareUpdateData() {
      return firmwareInteractor.firmwareCachePipe()
            .createObservableResult(new FirmwareUpdateCacheCommand(firmwareUpdateData));
   }

   private Observable clearFirmwareUpdateCache() {
      return firmwareInteractor.firmwareCachePipe().createObservableResult(new FirmwareUpdateCacheCommand(null));
   }

   private Observable clearFirmwareFilesCache() {
      return firmwareInteractor.clearFirmwareFilesPipe().createObservableResult(
            new FirmwareClearFilesCommand(firmwareUpdateData.firmwareFile().getParent()));
   }

   private Observable<SmartCard> activeSmartCard() {
      return janet.createPipe(ActiveSmartCardCommand.class)
            .observeSuccessWithReplay()
            .take(1)
            .map(Command::getResult);
   }

   private Observable<SmartCard> connectCard(SmartCard smartCard) {
      return janet.createPipe(ConnectSmartCardCommand.class)
            .createObservableResult(new ConnectSmartCardCommand(smartCard, false, true))
            .map(Command::getResult);
   }

   public int getCurrentStep() {
      if (loadFirmwareFilesCommand == null) return 0;
      else return loadFirmwareFilesCommand.getCurrentStep();
   }
}

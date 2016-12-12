package com.worldventures.dreamtrips.wallet.service.command.firmware;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import io.techery.janet.smartcard.action.support.UpgradeFirmwareAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.CommandActionBaseHelper.ActionCommandSubscriber.wrap;
import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.DFU;
import static rx.Observable.just;

@CommandAction
public class InstallFirmwareCommand extends Command implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject TemporaryStorage temporaryStorage;
   @Inject SnappyRepository snappyRepository;

   private final FirmwareUpdateData firmwareUpdateData;

   public InstallFirmwareCommand(FirmwareUpdateData firmwareUpdateData) {
      this.firmwareUpdateData = firmwareUpdateData;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      cacheFirmwareUpdateData()
            .flatMap(aVoid -> activeSmartCard()
                  .flatMap(sc -> prepareCardAndInstallFirmware(sc))
                  .flatMap(sc -> saveNewFirmwareVersion(sc))
            )
            .flatMap(aVoid -> clearFirmwareUpdateCache())
            .flatMap(aVoid -> clearFirmwareFilesCache())
            .subscribe(wrap(callback));
   }

   private Observable<SmartCard> prepareCardAndInstallFirmware(SmartCard smartCard) {
      return prepareSmartCard(smartCard)
            .flatMap(it -> {
               if (it.connectionStatus() == CONNECTED) return enableLockUnlockDevice(false);
               else if (it.connectionStatus() == DFU) return just(it);
               else return Observable.error(new IllegalStateException("Can't connect to card on firmwareUpdateData upgrade"));
            })
            .map(it -> firmwareUpdateData.firmwareFile())
            .flatMap(this::installFirmware)
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

   private Observable<Void> installFirmware(File file) {
      return janet.createPipe(UpgradeFirmwareAction.class)
            .createObservableResult(new UpgradeFirmwareAction(file))
            .flatMap(action ->//todo remove it when temporary storage will be useless
                  temporaryStorage.failInstall() ? Observable.error(new RuntimeException()) : Observable.just(action))
            .map(it -> (Void) null);

   }

   private Observable saveNewFirmwareVersion(SmartCard smartCard) {
      return Observable.create(subscriber -> {
         snappyRepository.saveSmartCard(ImmutableSmartCard
               .builder()
               .from(smartCard)
               .sdkVersion(firmwareUpdateData.firmwareInfo().sdkVersion())
               .firmWareVersion(firmwareUpdateData.firmwareInfo().firmwareVersion())
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
      return janet.createPipe(GetActiveSmartCardCommand.class)
            .createObservableResult(new GetActiveSmartCardCommand())
            .map(Command::getResult);
   }

   private Observable<SmartCard> connectCard(SmartCard smartCard) {
      return janet.createPipe(ConnectSmartCardCommand.class)
            .createObservableResult(new ConnectSmartCardCommand(smartCard, false, true))
            .map(Command::getResult);
   }

}

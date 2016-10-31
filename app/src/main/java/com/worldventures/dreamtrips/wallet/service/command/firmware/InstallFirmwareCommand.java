package com.worldventures.dreamtrips.wallet.service.command.firmware;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareDescriptor;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
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

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.DFU;
import static rx.Observable.just;

@CommandAction
public class InstallFirmwareCommand extends Command<Void> implements InjectableAction, FirmwareVersionCacheCommand {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject TemporaryStorage temporaryStorage;
   @Inject SnappyRepository snappyRepository;

   private final FirmwareDescriptor firmwareDescriptor;

   public InstallFirmwareCommand(FirmwareDescriptor firmwareDescriptor) {
      this.firmwareDescriptor = firmwareDescriptor;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      activeSmartCard()
            .flatMap(this::prepareCardAndInstallFirmware)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> prepareCardAndInstallFirmware(SmartCard smartCard) {
      return prepareSmartCard(smartCard)
            .flatMap(it -> {
               if (it.connectionStatus() == CONNECTED) return enableLockUnlockDevice(false);
               else if (it.connectionStatus() == DFU) return just(it);
               else return Observable.error(new IllegalStateException("Can't connect to card on firmware upgrade"));
            })
            .map(it -> firmwareDescriptor.firmwareFile())
            .flatMap(this::installFirmware)
            .flatMap(aVoid -> saveNewFirmwareVersion(smartCard))
            .doOnNext(aVoid -> enableLockUnlockDevice(true));
   }

   private Observable<SmartCard> prepareSmartCard(SmartCard smartCard) {
      return smartCard.connectionStatus() == CONNECTED
            || smartCard.connectionStatus() == DFU ? just(smartCard) : connectCard(smartCard);
   }

   private Observable<Void> saveNewFirmwareVersion(SmartCard smartCard) {
      return Observable.create(subscriber -> {
         snappyRepository.saveSmartCard(ImmutableSmartCard
               .builder()
               .from(smartCard)
               .sdkVersion(firmwareDescriptor.sdkVersion())
               .firmWareVersion(firmwareDescriptor.firmwareVersion())
               .build());
         //
         if (subscriber.isUnsubscribed()) return;
         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }

   private Observable<Void> enableLockUnlockDevice(boolean enable) {
      return smartCardInteractor.enableLockUnlockDeviceActionPipe()
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

   private Observable<SmartCard> activeSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .map(Command::getResult);
   }

   private Observable<SmartCard> connectCard(SmartCard smartCard) {
      return smartCardInteractor.connectActionPipe()
            .createObservableResult(new ConnectSmartCardCommand(smartCard, false, true))
            .map(Command::getResult);
   }

   @Override
   public String sdkVersion() {
      return firmwareDescriptor.sdkVersion();
   }

   @Override
   public String firmwareVersion() {
      return firmwareDescriptor.firmwareVersion();
   }
}

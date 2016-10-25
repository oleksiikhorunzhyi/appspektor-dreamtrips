package com.worldventures.dreamtrips.wallet.service.command.firmware;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
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
public class InstallFirmwareCommand extends Command<Void> implements InjectableAction {


   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject TemporaryStorage temporaryStorage;
   private final File file;

   public InstallFirmwareCommand(File file) {
      this.file = file;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      activeSmartCard()
            .map(Command::getResult)
            .flatMap(it -> it.connectionStatus() == CONNECTED || it.connectionStatus() == DFU ? just(it) : connectCard(it))
            .flatMap(it -> enableLockUnlockDevice(false))
            .flatMap(it -> Observable.just(file))
            .flatMap(this::installFirmware)
            .doOnNext(it -> enableLockUnlockDevice(true))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> enableLockUnlockDevice(boolean enable) {
      return smartCardInteractor.enableLockUnlockDeviceActionPipe()
            .createObservableResult(new EnableLockUnlockDeviceAction(enable))
            .map(action -> (Void) null);
   }

   private Observable<Void> installFirmware(File file) {
      return janet.createPipe(UpgradeFirmwareAction.class)
            .createObservableResult(new UpgradeFirmwareAction(file))
            .flatMap(action ->//todo remove it when temporary storage will be useless
                  temporaryStorage.failInstall() ? Observable.error(new RuntimeException()) : Observable.just(action))
            .map(it -> (Void) null);

   }

   private Observable<GetActiveSmartCardCommand> activeSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand());
   }

   private Observable<ConnectSmartCardCommand> connectCard(SmartCard smartCard) {
      return smartCardInteractor.connectActionPipe()
            .createObservableResult(new ConnectSmartCardCommand(smartCard));
   }
}

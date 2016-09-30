package com.worldventures.dreamtrips.wallet.service.command.firmware;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.util.RetryWithDelay;

import org.apache.commons.io.FileUtils;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.UpgradeFirmwareAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;
import static rx.Observable.just;

@CommandAction
public class InstallFirmwareCommand extends Command<Void> implements InjectableAction {

   private static final int MAX_RETRIES = 3;
   private static final int RETRY_DELAY_MILLIS = 10000;

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   private final File file;

   public InstallFirmwareCommand(File file) {
      this.file = file;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      activeSmartCard()
            .map(Command::getResult)
            .flatMap(it -> it.connectionStatus() == CONNECTED ? just(it) : connectCard(it))
            .flatMap(it -> Observable.just(file))
            .flatMap(this::fileToBytes)
            .flatMap(this::installFirmware)
            .flatMap(it -> activeSmartCard())
            .flatMap(it -> connectCard(it.getResult()))
            .map(it -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<? extends byte[]> fileToBytes(File file) {
      return Observable.fromCallable(() -> FileUtils.readFileToByteArray(file));
   }

   private Observable<UpgradeFirmwareAction> installFirmware(byte[] it) {
      return janet.createPipe(UpgradeFirmwareAction.class)
            .createObservableResult(new UpgradeFirmwareAction(it));
   }

   private Observable<GetActiveSmartCardCommand> activeSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand());
   }

   private Observable<ConnectSmartCardCommand> connectCard(SmartCard smartCard) {
      return smartCardInteractor.connectActionPipe()
            .createObservableResult(new ConnectSmartCardCommand(smartCard))
            .retryWhen(new RetryWithDelay(MAX_RETRIES, RETRY_DELAY_MILLIS));
   }
}

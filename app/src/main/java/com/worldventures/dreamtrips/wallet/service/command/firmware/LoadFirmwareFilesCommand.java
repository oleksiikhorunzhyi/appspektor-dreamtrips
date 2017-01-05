package com.worldventures.dreamtrips.wallet.service.command.firmware;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareVersions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.*;

@CommandAction
public class LoadFirmwareFilesCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   private final File fileArchive;
   private final FirmwareVersions availableFirmwareVersions;
   private final SmartCardFirmware smartCardFirmware;

   private int step = 0;

   private CommandCallback<Void> callback;
   private ActionPipe<LoadPuckAtmelFirmwareCommand> loadPuckAtmelFirmwareCommandActionPipe;
   private ActionPipe<LoadAppAtmelFirmwareCommand> loadAppAtmelFirmwareCommandActionPipe;
   private ActionPipe<LoadNordicFirmwareCommand> loadNordicFirmwareCommandActionPipe;

   public LoadFirmwareFilesCommand(File fileArchive, SmartCardFirmware smartCardFirmware, FirmwareVersions availableFirmwareVersions) {
      this.fileArchive = fileArchive;
      this.smartCardFirmware = smartCardFirmware;
      this.availableFirmwareVersions = availableFirmwareVersions;
   }

   int getCurrentStep() {
      return step;
   }

   @Override
   protected void run(CommandCallback<Void> callback) {
      this.callback = callback;
      loadPuckAtmelFirmwareCommandActionPipe = janet.createPipe(LoadPuckAtmelFirmwareCommand.class);
      loadAppAtmelFirmwareCommandActionPipe = janet.createPipe(LoadAppAtmelFirmwareCommand.class);
      loadNordicFirmwareCommandActionPipe = janet.createPipe(LoadNordicFirmwareCommand.class);
      Subscription subscription = listenCommandProgress(callback);

      janet.createPipe(UnzipFilesCommand.class)
            .createObservableResult(new UnzipFilesCommand(fileArchive))
            .map(Command::getResult)
            .flatMap(this::loadPuckAtmelFirmware)
            .flatMap(this::loadAppAtmelFirmware)
            .flatMap(this::loadNordicBootloaderFirmware)
            .flatMap(this::loadNordicAppFirmware)
            .subscribe(command -> {
               subscription.unsubscribe();
               callback.onSuccess(null);
            }, throwable -> {
               Timber.e(throwable, "Error while loading firmwares");
               callback.onFail(throwable);
            });
   }

   private Subscription listenCommandProgress(CommandCallback<Void> callback) {
      return Observable.merge(
               loadPuckAtmelFirmwareCommandActionPipe.observe(),
               loadAppAtmelFirmwareCommandActionPipe.observe(),
               loadNordicFirmwareCommandActionPipe.observe())
            .filter(actionState -> actionState.status == ActionState.Status.PROGRESS)
            .subscribe(actionState -> callback.onProgress(actionState.progress));
   }

   private Observable<UnzipFilesCommand.FirmwareBundle> loadPuckAtmelFirmware(UnzipFilesCommand.FirmwareBundle fileBundle) {
      notifyNewInstallStep();
      return !newFirmwareAvailable(smartCardFirmware.externalAtmelVersion(), availableFirmwareVersions.puckAtmelVerstion()) ?
            Observable.just(fileBundle) :
            loadPuckAtmelFirmwareCommandActionPipe
                  .createObservableResult(new LoadPuckAtmelFirmwareCommand(fileBundle.puckAtmel(), availableFirmwareVersions
                        .puckAtmelVerstion()))
                  .map(command -> fileBundle);
   }

   private Observable<UnzipFilesCommand.FirmwareBundle> loadAppAtmelFirmware(UnzipFilesCommand.FirmwareBundle fileBundle) {
      notifyNewInstallStep();
      return !newFirmwareAvailable(smartCardFirmware.internalAtmelVersion(), availableFirmwareVersions.atmelVersion()) ?
            Observable.just(fileBundle) :
            loadAppAtmelFirmwareCommandActionPipe
                  .createObservableResult(new LoadAppAtmelFirmwareCommand(fileBundle.appAtmel(), availableFirmwareVersions.atmelVersion()))
                  .map(command -> fileBundle);
   }

   private Observable<UnzipFilesCommand.FirmwareBundle> loadNordicBootloaderFirmware(UnzipFilesCommand.FirmwareBundle fileBundle) {
      notifyNewInstallStep();
      return !newFirmwareAvailable(smartCardFirmware.nrfBootloaderVersion(), availableFirmwareVersions.bootloaderNordicVersion()) ?
            Observable.just(fileBundle) :
            loadNordicFirmwareCommandActionPipe
                  .createObservableResult(new LoadNordicFirmwareCommand(fileBundle.booloaderNordic(),
                        availableFirmwareVersions.bootloaderNordicVersion(), true))
                  .map(command -> fileBundle);
}

   private Observable<UnzipFilesCommand.FirmwareBundle> loadNordicAppFirmware(UnzipFilesCommand.FirmwareBundle fileBundle) {
      notifyNewInstallStep();

      //If app must perform a bootloader upgrade, then app MUST ALWAYS perform app nordic upgrade
      return !newFirmwareAvailable(smartCardFirmware.nrfBootloaderVersion(), availableFirmwareVersions.bootloaderNordicVersion())
            && !newFirmwareAvailable(smartCardFirmware.firmwareVersion(), availableFirmwareVersions.nordicVersion()) ?
            Observable.just(fileBundle) :
            loadNordicFirmwareCommandActionPipe
                  .createObservableResult(new LoadNordicFirmwareCommand(fileBundle.appNordic(),
                        availableFirmwareVersions.nordicVersion(), false))
                  .map(command -> fileBundle);
   }

   private void notifyNewInstallStep() {
      step++;
      callback.onProgress(0);
   }

}

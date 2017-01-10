package com.worldventures.dreamtrips.wallet.service.command.firmware;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareVersions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ConnectionType;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.newFirmwareAvailable;

@CommandAction
public class LoadFirmwareFilesCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   private final File fileArchive;
   private final FirmwareVersions availableFirmwareVersions;
   private final SmartCardFirmware smartCardFirmware;
   private final boolean dfuMode;

   private int step = 0;

   private CommandCallback<Void> callback;
   private ActionPipe<LoadPuckAtmelFirmwareCommand> loadPuckAtmelFirmwareCommandActionPipe;
   private ActionPipe<LoadAppAtmelFirmwareCommand> loadAppAtmelFirmwareCommandActionPipe;
   private ActionPipe<LoadNordicFirmwareCommand> loadNordicFirmwareCommandActionPipe;

   public LoadFirmwareFilesCommand(File fileArchive, SmartCardFirmware smartCardFirmware,
         FirmwareVersions availableFirmwareVersions, boolean dfuMode) {
      this.fileArchive = fileArchive;
      this.smartCardFirmware = smartCardFirmware;
      this.availableFirmwareVersions = availableFirmwareVersions;
      this.dfuMode = dfuMode;
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
      if (newFirmwareAvailable(smartCardFirmware.externalAtmelVersion(), availableFirmwareVersions.puckAtmelVerstion())) {
         return loadPuckAtmelFirmwareCommandActionPipe
               .createObservableResult(new LoadPuckAtmelFirmwareCommand(fileBundle.puckAtmel(), availableFirmwareVersions
                     .puckAtmelVerstion()))
               .map(command -> fileBundle);
      } else {
         return Observable.just(fileBundle);
      }
   }

   private Observable<UnzipFilesCommand.FirmwareBundle> loadAppAtmelFirmware(UnzipFilesCommand.FirmwareBundle fileBundle) {
      notifyNewInstallStep();
      if (newFirmwareAvailable(smartCardFirmware.internalAtmelVersion(), availableFirmwareVersions.atmelVersion())) {
         return loadAppAtmelFirmwareCommandActionPipe
               .createObservableResult(new LoadAppAtmelFirmwareCommand(fileBundle.appAtmel(), availableFirmwareVersions.atmelVersion()))
               .map(command -> fileBundle);
      } else {
         return Observable.just(fileBundle);
      }
   }

   private Observable<UnzipFilesCommand.FirmwareBundle> loadNordicBootloaderFirmware(UnzipFilesCommand.FirmwareBundle fileBundle) {
      notifyNewInstallStep();
      if (dfuMode || newFirmwareAvailable(smartCardFirmware.nrfBootloaderVersion(), availableFirmwareVersions.bootloaderNordicVersion())) {
         return loadNordicFirmwareCommandActionPipe
               .createObservableResult(new LoadNordicFirmwareCommand(fileBundle.booloaderNordic(),
                     availableFirmwareVersions.bootloaderNordicVersion(), true))
               .flatMap(loadNordicFirmwareCommand -> janet.createPipe(ConnectAction.class) // waiting for restart to DFU mode
                     .observeSuccessWithReplay()
                     .map(connectAction -> connectAction.type)
                     .filter(connectionType -> connectionType == ConnectionType.DFU)
                     .timeout(5, TimeUnit.MINUTES)
                     .take(1))
               .map(command -> fileBundle);
      } else {
         return Observable.just(fileBundle);
      }
   }

   private Observable<UnzipFilesCommand.FirmwareBundle> loadNordicAppFirmware(UnzipFilesCommand.FirmwareBundle fileBundle) {
      notifyNewInstallStep();
      //If app must perform a bootloader upgrade, then app MUST ALWAYS perform app nordic upgrade
      if (dfuMode || newFirmwareAvailable(smartCardFirmware.nordicAppVersion(), availableFirmwareVersions.nordicVersion())
            || newFirmwareAvailable(smartCardFirmware.nrfBootloaderVersion(), availableFirmwareVersions.bootloaderNordicVersion())) {
         return loadNordicFirmwareCommandActionPipe
               .createObservableResult(new LoadNordicFirmwareCommand(fileBundle.appNordic(),
                     availableFirmwareVersions.nordicVersion(), false))
               .map(command -> fileBundle);
      } else {
         return Observable.just(fileBundle);
      }
   }

   private void notifyNewInstallStep() {
      step++;
      callback.onProgress(0);
   }

}

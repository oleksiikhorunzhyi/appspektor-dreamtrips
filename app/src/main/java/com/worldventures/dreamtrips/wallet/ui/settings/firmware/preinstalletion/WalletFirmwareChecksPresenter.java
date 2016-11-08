package com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.firmware.PreInstallationCheckCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.install.WalletInstallFirmwarePath;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;

public class WalletFirmwareChecksPresenter extends WalletPresenter<WalletFirmwareChecksPresenter.Screen, Parcelable> {

   @Inject WalletBluetoothService bluetoothService;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject Navigator navigator;

   private final String firmwareFilePath;
   private final FirmwareInfo firmwareInfo;

   WalletFirmwareChecksPresenter(Context context, Injector injector, String firmwareFilePath, FirmwareInfo firmwareInfo) {
      super(context, injector);
      this.firmwareFilePath = firmwareFilePath;
      this.firmwareInfo = firmwareInfo;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeChecks();
   }

   private void observeChecks() {
      firmwareInteractor.preInstallationCheckPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bind(command.getResult()));

      Observable.concat(
            smartCardInteractor.smartCardModifierPipe()
                  .observeSuccessWithReplay().map(command -> (Void) null),
            bluetoothService.observeEnablesState().map(value -> (Void) null)
      )
            .compose(bindView())
            .subscribe(aVoid -> firmwareInteractor
                  .preInstallationCheckPipe().send(new PreInstallationCheckCommand()));
   }

   void installLater() {
      goBack();
   }

   void install() {
      navigator.go(new WalletInstallFirmwarePath(
            ImmutableFirmwareUpdateData.builder()
                  .firmwareInfo(firmwareInfo)
                  .firmwareFile(new File(firmwareFilePath))
                  .updateAvailable(true)
                  .build())
      );
   }

   void goBack() {
      navigator.goBack();
   }

   private void bind(PreInstallationCheckCommand.Checks checks) {
      Screen view = getView();
      boolean bluetoothEnabled = checks.bluetoothIsEnabled();
      boolean connected = checks.smartCardIsConnected();
      boolean charged = checks.smartCardIsCharged();
      //noinspection all
      view.connectionStatusVisible(bluetoothEnabled); // secont item
      view.chargedStatusVisible(bluetoothEnabled && connected); // third item

      view.bluetoothEnabled(bluetoothEnabled);
      view.cardConnected(connected);
      view.cardCharged(charged);

      view.installButtonEnabled(bluetoothEnabled && connected && charged);
   }

   public interface Screen extends WalletScreen {

      void bluetoothEnabled(boolean enabled);

      void cardConnected(boolean connected);

      void cardCharged(boolean charged);

      void connectionStatusVisible(boolean isVisible);

      void chargedStatusVisible(boolean isVisible);

      void installButtonEnabled(boolean enabled);
   }
}

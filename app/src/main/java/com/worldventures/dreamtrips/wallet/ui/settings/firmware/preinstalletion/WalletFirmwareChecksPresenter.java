package com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.UpdateChecksVisitAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.UpdateInstallAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.UpdateInstallLaterAction;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PreInstallationCheckCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.install.WalletInstallFirmwarePath;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class WalletFirmwareChecksPresenter extends WalletPresenter<WalletFirmwareChecksPresenter.Screen, Parcelable> {

   @Inject WalletBluetoothService bluetoothService;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Navigator navigator;

   WalletFirmwareChecksPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      trackScreen();
      observeChecks();
   }

   private void trackScreen() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new UpdateChecksVisitAction()));
   }

   private void observeChecks() {
      firmwareInteractor.preInstallationCheckPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bind(command.getResult()), throwable -> {
            });

      Observable.merge(
            bluetoothService.observeEnablesState(),
            firmwareInteractor.connectActionPipe().observeSuccess(),
            smartCardInteractor.cardInChargerEventPipe().observeSuccess()
      ).debounce(500, TimeUnit.MILLISECONDS)
            .compose(bindView())
            .subscribe(aVoid -> firmwareInteractor
                  .preInstallationCheckPipe()
                  .send(new PreInstallationCheckCommand()), throwable -> Timber.e(throwable, ""));
      firmwareInteractor
            .preInstallationCheckPipe().send(new PreInstallationCheckCommand());
   }

   void installLater() {
      goBack();
      analyticsInteractor.walletFirmwareAnalyticsPipe()
                  .send(new WalletFirmwareAnalyticsCommand(new UpdateInstallLaterAction()));
   }

   void install() {
      navigator.go(new WalletInstallFirmwarePath());
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new UpdateInstallAction()));
   }

   void goBack() {
      navigator.goBack();
   }

   private void bind(PreInstallationCheckCommand.Checks checks) {
      Screen view = getView();
      boolean bluetoothEnabled = checks.bluetoothIsEnabled();
      boolean connected = checks.smartCardIsConnected();
      boolean charged = checks.smartCardIsCharged();
      boolean cardInCharger = checks.connectToCharger();
      boolean cardInChargerRequired = checks.connectCardToChargerRequired();
      //noinspection all
      view.connectionStatusVisible(bluetoothEnabled); // secont item
      view.chargedStatusVisible(bluetoothEnabled && connected); // third item
      view.cardIsInChargerCheckVisible(bluetoothEnabled && connected && charged && cardInChargerRequired);

      view.bluetoothEnabled(bluetoothEnabled);
      view.cardConnected(connected);
      view.cardCharged(charged);
      view.cardIsInCharger(cardInCharger);

      view.installButtonEnabled(bluetoothEnabled && connected && charged && (!cardInChargerRequired || cardInCharger));
   }

   public interface Screen extends WalletScreen {

      void bluetoothEnabled(boolean enabled);

      void cardConnected(boolean connected);

      void cardCharged(boolean charged);

      void connectionStatusVisible(boolean isVisible);

      void chargedStatusVisible(boolean isVisible);

      void installButtonEnabled(boolean enabled);

      void cardIsInCharger(boolean enabled);

      void cardIsInChargerCheckVisible(boolean isVisible);
   }
}

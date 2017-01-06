package com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.UpdateChecksVisitAction;
import com.worldventures.dreamtrips.wallet.analytics.UpdateInstallAction;
import com.worldventures.dreamtrips.wallet.analytics.UpdateInstallLaterAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.firmware.PreInstallationCheckCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.install.WalletInstallFirmwarePath;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;

public class WalletFirmwareChecksPresenter extends WalletPresenter<WalletFirmwareChecksPresenter.Screen, Parcelable> {

   @Inject WalletBluetoothService bluetoothService;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Navigator navigator;

   private final String firmwareFilePath;
   private final FirmwareInfo firmwareInfo;
   private final SmartCard smartCard;

   WalletFirmwareChecksPresenter(SmartCard smartCard, Context context, Injector injector, String firmwareFilePath, FirmwareInfo firmwareInfo) {
      super(context, injector);
      this.firmwareFilePath = firmwareFilePath;
      this.firmwareInfo = firmwareInfo;
      this.smartCard = smartCard;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeChecks();
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();

      WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(smartCard, new UpdateChecksVisitAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   private void observeChecks() {
      firmwareInteractor.preInstallationCheckPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> bind(command.getResult()), throwable -> {});

      Observable.merge(
            bluetoothService.observeEnablesState(),
            smartCardInteractor.cardInChargerEventPipe().observeSuccess()
      ).debounce(500, TimeUnit.MILLISECONDS)
            .compose(bindView())
            .subscribe(aVoid -> firmwareInteractor
                  .preInstallationCheckPipe().send(new PreInstallationCheckCommand(firmwareInfo)), throwable -> {
            });
      firmwareInteractor
            .preInstallationCheckPipe().send(new PreInstallationCheckCommand(smartCard, firmwareInfo));
   }

   void installLater() {
      goBack();

      WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(smartCard, new UpdateInstallLaterAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   void install() {
      WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(smartCard, new UpdateInstallAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);

      navigator.go(new WalletInstallFirmwarePath(smartCard,
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

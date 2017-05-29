package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.PreCheckNewCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import timber.log.Timber;

public class NewCardPowerOnPresenter extends WalletPresenter<NewCardPowerOnPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FactoryResetInteractor factoryResetInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject WalletBluetoothService bluetoothService;
   private final CheckPinDelegate checkPinDelegate;

   public NewCardPowerOnPresenter(Context context, Injector injector) {
      super(context, injector);
      checkPinDelegate = new CheckPinDelegate(smartCardInteractor, factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.NEW_CARD);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      checkPinDelegate.observePinStatus(getView());
      fetchSmartCardId();
   }

   private void fetchSmartCardId() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().setTitleWithSmartCardID(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
            );
   }

   void cantTurnOnSmartCard() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().showConfirmationUnassignOnBackend(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
            );
   }

   void unassignCardOnBackend() {
      smartCardInteractor.wipeSmartCardDataCommandActionPipe()
            .createObservable(new WipeSmartCardDataCommand(ResetOptions.builder()
                  .wipePaymentCards(false)
                  .wipeUserSmartCardData(false)
                  .build()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideWipeOperationView())
                  .onSuccess(activeSmartCardCommand -> navigator.single(new UnassignSuccessPath()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
                  .create());
   }

   void navigateNext() {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> handleConnectionSmartCard(bluetoothService.isEnable(), command.getResult()
                        .connectionStatus()
                        .isConnected()))
                  .onFail((command, throwable) -> navigator.go(new PreCheckNewCardPath())));
   }

   private void handleConnectionSmartCard(boolean bluetoothIsConnected, boolean smartCardConnected) {
      if (bluetoothIsConnected && smartCardConnected) {
         checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
      } else {
         navigator.go(new PreCheckNewCardPath());
      }
   }

   public void goBack() {
      navigator.goBack();
   }

   void retryFactoryReset() {
      checkPinDelegate.getFactoryResetDelegate().factoryReset();
   }

   public interface Screen extends WalletScreen, FactoryResetView {

      void setTitleWithSmartCardID(String scID);

      void showConfirmationUnassignOnBackend(String scId);

      OperationView<WipeSmartCardDataCommand> provideWipeOperationView();
   }
}

package com.worldventures.dreamtrips.wallet.ui.settings.newcard.poweron;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.check.PreCheckNewCardPath;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.pin.EnterPinUnassignPath;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.success.UnassignSuccessPath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import timber.log.Timber;

public class NewCardPowerOnPresenter extends WalletPresenter<NewCardPowerOnPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletBluetoothService bluetoothService;

   public NewCardPowerOnPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
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
            .createObservable(new WipeSmartCardDataCommand(false))
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
         navigator.go(new EnterPinUnassignPath());
      } else {
         navigator.go(new PreCheckNewCardPath());
      }
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void setTitleWithSmartCardID(String scID);

      void showConfirmationUnassignOnBackend(String scId);

      OperationView<WipeSmartCardDataCommand> provideWipeOperationView();
   }
}

package com.worldventures.dreamtrips.wallet.ui.settings.newcard.check;

import android.content.Context;
import android.os.Parcelable;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.pin.EnterPinUnassignPath;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class PreCheckNewCardPresenter extends WalletPresenter<PreCheckNewCardPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletBluetoothService bluetoothService;

   public PreCheckNewCardPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeChecks();
   }

   private void observeChecks() {
      Observable.combineLatest(
            smartCardInteractor.activeSmartCardPipe()
                  .observeSuccess()
                  .throttleLast(300, TimeUnit.MILLISECONDS),
            bluetoothService.observeEnablesState()
                  .startWith(bluetoothService.isEnable()),
            (smartCardCommand, bluetoothIsEnabled) -> new Pair<>(bluetoothIsEnabled, smartCardCommand.getResult()))
            .compose(bindViewIoToMainComposer())
            .subscribe(pair -> bind(pair.first, pair.second.connectionStatus()
                  .isConnected()), throwable -> Timber.e(throwable, ""));

      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
   }

   private void bind(boolean bluetoothIsEnabled, boolean smartCardConnected) {
      getView().bluetoothEnable(bluetoothIsEnabled);
      getView().setVisiblePowerSmartCardWidget(bluetoothIsEnabled);
      getView().cardConnected(smartCardConnected);
      getView().nextButtonEnabled(bluetoothIsEnabled && smartCardConnected);
   }

   void prepareContinueAddCard() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().showAddCardContinueDialog(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
            );
   }

   void navigateNext() {
      navigator.go(new EnterPinUnassignPath());
   }

   void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void showAddCardContinueDialog(String scId);

      void nextButtonEnabled(boolean enable);

      void bluetoothEnable(boolean enabled);

      void cardConnected(boolean enabled);

      void setVisiblePowerSmartCardWidget(boolean visible);
   }
}

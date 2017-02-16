package com.worldventures.dreamtrips.wallet.ui.settings.newcard.check;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.pin.EnterPinUnassignPath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class PreCheckNewCardPresenter extends WalletPresenter<PreCheckNewCardPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletBluetoothService bluetoothService;

   public PreCheckNewCardPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeCheckBluetooth();
   }

   private void observeCheckBluetooth() {
      bluetoothService.observeEnablesState()
            .startWith(bluetoothService.isEnable())
            .compose(bindView())
            .subscribe(this::bind);
   }

   private void bind(boolean enable) {
      getView().bluetoothEnable(enable);
      getView().nextButtonEnabled(enable);
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
   }
}

package com.worldventures.dreamtrips.wallet.ui.settings.factory_reset;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.factory_reset_success.FactoryResetSuccessPath;

import javax.inject.Inject;

import io.techery.janet.CancelException;
import io.techery.janet.smartcard.action.lock.LockDeviceAction;

public class FactoryResetPresenter extends WalletPresenter<FactoryResetPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FactoryResetInteractor factoryResetInteractor;
   @Inject Navigator navigator;

   public FactoryResetPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      resetSmartCard();
      observeUnlockCard();
   }

   private void resetSmartCard() {
      factoryResetInteractor.factoryResetCommandActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<FactoryResetCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> navigator.single(new FactoryResetSuccessPath()))
                  .onFail(ErrorHandler.<FactoryResetCommand>builder(getContext())
                        .ignore(CancelException.class)
                        .defaultAction(command ->  goBack())
                        .build())
                  .wrap()
            );

      factoryResetInteractor.factoryResetCommandActionPipe().send(new FactoryResetCommand(true));
   }

   private void observeUnlockCard() {
      smartCardInteractor.lockDeviceChangedEventPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .filter(lockDeviceChangedEvent -> !lockDeviceChangedEvent.locked)
            .take(1)
            .subscribe(lockDeviceChangedEvent ->  getView().restrictCancel());
   }

   public void cancelFactoryReset() {
      factoryResetInteractor.factoryResetCommandActionPipe().cancelLatest();
      factoryResetInteractor.lockDevicePipe().send(new LockDeviceAction(false));
      goBack();
   }
   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void restrictCancel();
   }
}

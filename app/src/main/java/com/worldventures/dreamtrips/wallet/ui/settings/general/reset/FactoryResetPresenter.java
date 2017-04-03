package com.worldventures.dreamtrips.wallet.ui.settings.general.reset;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.FactoryResetSuccessPath;

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
   }

   @Override
   public void detachView(boolean retainInstance) {
      cancelFactoryReset();
      super.detachView(retainInstance);
   }

   private void resetSmartCard() {
      factoryResetInteractor.resetSmartCardCommandActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<ResetSmartCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> navigator.single(new FactoryResetSuccessPath()))
                  .onFail(ErrorHandler.<ResetSmartCardCommand>builder(getContext())
                        .ignore(CancelException.class)
                        .defaultAction(command -> goBack())
                        .build())
                  .wrap()
            );

      factoryResetInteractor.factoryResetCommandActionPipe().send(new FactoryResetCommand(
                  ResetOptions.builder()
                        .withEnterPin(true)
                        .build())
      );
   }

   private void cancelFactoryReset() {
      factoryResetInteractor.factoryResetCommandActionPipe().cancelLatest();
      factoryResetInteractor.lockDevicePipe().send(new LockDeviceAction(false));
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }
}

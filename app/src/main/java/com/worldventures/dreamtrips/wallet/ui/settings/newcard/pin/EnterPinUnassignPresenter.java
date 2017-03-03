package com.worldventures.dreamtrips.wallet.ui.settings.newcard.pin;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.EnterPinUnAssignAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.EnterPinUnAssignEnteredAction;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.success.UnassignSuccessPath;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.lock.LockDeviceAction;

public class EnterPinUnassignPresenter extends WalletPresenter<EnterPinUnassignPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FactoryResetInteractor factoryResetInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public EnterPinUnassignPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      analyticsInteractor
            .walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new EnterPinUnAssignAction()));

      observeUnassignSmartCard();
   }

   private void observeUnassignSmartCard() {
      factoryResetInteractor.factoryResetCommandActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> {
                     analyticsInteractor
                           .walletAnalyticsCommandPipe()
                           .send(new WalletAnalyticsCommand(new EnterPinUnAssignEnteredAction()));

                     navigator.single(new UnassignSuccessPath());
                  })
                  .onFail((factoryResetCommand, throwable) -> getView().showErrorEnterPinDialog())
                  .create());

      factoryResetInteractor.factoryResetCommandActionPipe().send(new FactoryResetCommand(true, false));
   }

   void retryEnterPinAndUnassign() {
      factoryResetInteractor.factoryResetCommandActionPipe().send(new FactoryResetCommand(true, false));
   }

   void cancelUnassign() {
      factoryResetInteractor.factoryResetCommandActionPipe().cancelLatest();
      factoryResetInteractor.lockDevicePipe().send(new LockDeviceAction(false));
      goBack();
   }

   private void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      OperationView<FactoryResetCommand> provideOperationView();

      void showErrorEnterPinDialog();
   }
}

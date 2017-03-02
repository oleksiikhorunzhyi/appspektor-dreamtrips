package com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.ResetPinAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.ResetPinSuccessAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.SetPinAction;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessPath;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;

import static com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action.RESET;
import static com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action.SETUP;

public class WizardPinSetupPresenter extends WalletPresenter<WizardPinSetupPresenter.Screen, Parcelable> {

   private final Action mode;

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public WizardPinSetupPresenter(Context context, Injector injector, Action mode) {
      super(context, injector);
      this.mode = mode;
   }

   public void goBack() {
      navigator.goBack();
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
   }

   private void trackScreen() {
      WalletAnalyticsAction walletAnalyticsAction = (mode == RESET)
            ? new ResetPinAction()
            : new SetPinAction();
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(walletAnalyticsAction));
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      view.showMode(mode);
      observeSetupFinishedPipe();
   }

   private void observeSetupFinishedPipe() {
      wizardInteractor.pinSetupFinishedPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<PinSetupFinishedEvent>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> {
                     if (mode == RESET) {
                        trackPinResetSuccess();
                     }
                     getView().provideOperationDelegate().hideProgress();
                     navigateToNextScreen();
                  })
                  .onFail(ErrorHandler.<PinSetupFinishedEvent>builder(getContext())
                        .defaultMessage(R.string.wallet_wizard_setup_error)
                        .build())
                  .wrap());

      wizardInteractor.startPinSetupPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<StartPinSetupAction>forView(getView().provideOperationDelegate())
                  .onFail(ErrorHandler.<StartPinSetupAction>builder(getContext())
                        .defaultMessage(R.string.wallet_wizard_setup_error)
                        .build())
                  .onStart(action -> getView().provideOperationDelegate().showProgress(null))
                  .wrap()
            );
   }

   private void trackPinResetSuccess() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new ResetPinSuccessAction()));
   }

   void setupPIN() {
      wizardInteractor.startPinSetupPipe().send(new StartPinSetupAction());
   }

   private void navigateToNextScreen() {
      if (mode == SETUP) {
         navigator.withoutLast(new WalletPinIsSetPath());
      } else {
         navigator.withoutLast(new PinSetSuccessPath(mode));
      }
   }

   public interface Screen extends WalletScreen {

      void showMode(Action mode);
   }
}

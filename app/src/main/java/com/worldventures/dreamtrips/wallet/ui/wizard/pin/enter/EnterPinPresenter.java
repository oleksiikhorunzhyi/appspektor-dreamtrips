package com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;

public class EnterPinPresenter extends WalletPresenter<EnterPinPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final EnterPinDelegate enterPinDelegate;

   EnterPinPresenter(Context context, Injector injector, Action mode) {
      super(context, injector);

      enterPinDelegate = EnterPinDelegate.create(mode, analyticsInteractor, navigator);
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
      enterPinDelegate.trackScreen();
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      enterPinDelegate.prepareView(view);
      observeSetupFinishedPipe();
   }

   private void observeSetupFinishedPipe() {
      wizardInteractor.pinSetupFinishedPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<PinSetupFinishedEvent>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> {
                     enterPinDelegate.pinEntered();
                     getView().provideOperationDelegate().hideProgress();
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

   void setupPIN() {
      wizardInteractor.startPinSetupPipe().send(new StartPinSetupAction());
   }

   public interface Screen extends WalletScreen, EnterPinDelegate.PinView {
   }
}

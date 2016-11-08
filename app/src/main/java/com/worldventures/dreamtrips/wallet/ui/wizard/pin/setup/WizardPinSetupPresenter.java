package com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.SetPinAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WalletPinIsSetPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessPath;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;

import static com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action.SETUP;

public class WizardPinSetupPresenter extends WalletPresenter<WizardPinSetupPresenter.Screen, Parcelable> {

   private final SmartCard smartCard;
   private final Action mode;

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public WizardPinSetupPresenter(Context context, Injector injector, SmartCard smartCard, Action mode) {
      super(context, injector);
      this.smartCard = smartCard;
      this.mode = mode;
   }

   public void goBack() {
      navigator.goBack();
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new SetPinAction(smartCard.cardName())));
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
            .delay(2, TimeUnit.SECONDS) // // TODO: 10/4/16 remove before release, MOCK delay
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<PinSetupFinishedEvent>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> {
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

   void setupPIN() {
      wizardInteractor.startPinSetupPipe().send(new StartPinSetupAction());
   }

   private void navigateToNextScreen() {
      if (mode == SETUP) {
         navigator.withoutLast(new WalletPinIsSetPath(smartCard));
      } else {
         navigator.withoutLast(new PinSetSuccessPath(mode));
      }
   }

   public interface Screen extends WalletScreen {

      void showMode(Action mode);
   }
}

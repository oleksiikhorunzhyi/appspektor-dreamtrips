package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WalletPinIsSetPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin_set_success.PinSetSuccessPath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;

import static com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPath.Action.SETUP;

public class WizardPinSetupPresenter extends WalletPresenter<WizardPinSetupPresenter.Screen, Parcelable> {

   private final SmartCard smartCard;
   private final WizardPinSetupPath.Action mode;

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;

   public WizardPinSetupPresenter(Context context, Injector injector, SmartCard smartCard, WizardPinSetupPath.Action mode) {
      super(context, injector);
      this.smartCard = smartCard;
      this.mode = mode;
   }

   public void goToBack() {
      navigator.goBack();
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      view.showMode(mode);
      observeSetupFinishedPipe();
   }

   private void observeSetupFinishedPipe() {
      wizardInteractor.pinSetupFinishedPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<PinSetupFinishedEvent>().onSuccess(action -> {
               getView().provideOperationDelegate().hideProgress();
               navigateToNextScreen();
            })
                  .onFail((action, throwable) -> getView().provideOperationDelegate()
                        .showError(getContext().getString(R.string.wallet_wizard_setup_error), null)));

      wizardInteractor.startPinSetupPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<StartPinSetupAction>().onFail((action1, throwable) -> getView().provideOperationDelegate()
                  .showError(getContext().getString(R.string.wallet_wizard_setup_error), null))
                  .onStart(action -> getView().provideOperationDelegate().showProgress()));
   }

   public void setupPIN() {
      wizardInteractor.startPinSetupPipe().send(new StartPinSetupAction());
   }

   private void navigateToNextScreen() {
      if (mode == SETUP) {
         navigator.single(new WalletPinIsSetPath(smartCard));
      } else {
         navigator.go(new PinSetSuccessPath());
      }
   }

   public interface Screen extends WalletScreen {

      void showMode(WizardPinSetupPath.Action mode);
   }
}

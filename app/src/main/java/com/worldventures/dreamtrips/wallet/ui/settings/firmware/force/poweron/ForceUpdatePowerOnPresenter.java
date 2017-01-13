package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.poweron;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCheckCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.pairkey.ForcePairKeyPath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class ForceUpdatePowerOnPresenter extends WalletPresenter<ForceUpdatePowerOnPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   //temporary solution due to new flow navigation
   @Inject WizardInteractor wizardInteractor;
   @Inject WalletBluetoothService bluetoothService;
   @Inject WalletNetworkService networkService;

   public ForceUpdatePowerOnPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeChecks();
      observeBluetoothAndNetwork();
   }

   private void observeBluetoothAndNetwork() {
      Observable.merge(bluetoothService.observeEnablesState(), networkService.observeConnectedState())
            .compose(bindViewIoToMainComposer())
            .subscribe(b -> wizardInteractor.checksPipe()
                  .send(new WizardCheckCommand()), throwable -> Timber.e(throwable, ""));
   }

   private void observeChecks() {
      wizardInteractor.checksPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<WizardCheckCommand>()
                  .onSuccess(command -> bindResult(command.getResult()))
            );
   }

   private void bindResult(WizardCheckCommand.Checks result) {
      getView().setButtonAction((result.internetIsAvailable() && result.bluetoothIsEnabled()) ? v -> openPairCardScreen() :
            v -> getView().showDialogEnableBleAndInternet());
   }

   public void openPairCardScreen() {
      navigator.single(new ForcePairKeyPath());
   }

   public void onBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void setButtonAction(View.OnClickListener onClickListener);

      void showDialogEnableBleAndInternet();
   }
}

package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.impl;


import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCheckCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletNetworkDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class ForceUpdatePowerOnPresenterImpl extends WalletPresenterImpl<ForceUpdatePowerOnScreen> implements ForceUpdatePowerOnPresenter {

   private final WalletNetworkDelegate networkDelegate;
   private final WizardInteractor wizardInteractor;
   private final WalletBluetoothService bluetoothService;

   public ForceUpdatePowerOnPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletNetworkDelegate networkDelegate, WizardInteractor wizardInteractor, WalletBluetoothService bluetoothService) {
      super(navigator, deviceConnectionDelegate);
      this.networkDelegate = networkDelegate;
      this.wizardInteractor = wizardInteractor;
      this.bluetoothService = bluetoothService;
   }

   @Override
   public void attachView(ForceUpdatePowerOnScreen view) {
      super.attachView(view);
      networkDelegate.setup(view);
      observeChecks();
      observeBluetoothAndNetwork();
   }

   private void observeBluetoothAndNetwork() {
      Observable.merge(bluetoothService.observeEnablesState(), networkDelegate.observeConnectedState())
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
      getNavigator().goForcePairKey();
   }

   @Override
   public void onBack() {
      getNavigator().goBack();
   }
}

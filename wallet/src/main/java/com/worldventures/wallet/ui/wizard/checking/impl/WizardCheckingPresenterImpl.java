package com.worldventures.wallet.ui.wizard.checking.impl;


import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.wizard.WizardCheckCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingPresenter;
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WizardCheckingPresenterImpl extends WalletPresenterImpl<WizardCheckingScreen> implements WizardCheckingPresenter {

   private final WalletNetworkDelegate networkDelegate;
   private final WizardInteractor wizardInteractor;
   private final WalletBluetoothService bluetoothService;

   public WizardCheckingPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletNetworkDelegate networkDelegate, WizardInteractor wizardInteractor, WalletBluetoothService bluetoothService) {
      super(navigator, deviceConnectionDelegate);
      this.networkDelegate = networkDelegate;
      this.wizardInteractor = wizardInteractor;
      this.bluetoothService = bluetoothService;
   }

   @Override
   public void attachView(WizardCheckingScreen view) {
      super.attachView(view);
      networkDelegate.setup(view);
      observeBluetoothAndNetwork();
      observeChecks();
      wizardInteractor.checksPipe().send(new WizardCheckCommand());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void goNext() {
      getNavigator().goWizardTerms();
   }

   private void observeBluetoothAndNetwork() {
      Observable.merge(bluetoothService.observeEnablesState(), networkDelegate.observeConnectedState())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(b -> wizardInteractor.checksPipe().send(new WizardCheckCommand()));
   }

   private void observeChecks() {
      wizardInteractor.checksPipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<WizardCheckCommand>()
                  .onSuccess(command -> bindResult(command.getResult()))
            );
   }

   private void bindResult(WizardCheckCommand.Checks result) {
      final WizardCheckingScreen view = getView();
      //noinspection all
      view.networkAvailable(result.internetIsAvailable());
      view.bluetoothEnable(result.bluetoothIsEnabled());
      if (!result.bleIsSupported()) {
         view.bluetoothDoesNotSupported();
      }
      view.buttonEnable(result.internetIsAvailable() && result.bluetoothIsEnabled());
   }
}

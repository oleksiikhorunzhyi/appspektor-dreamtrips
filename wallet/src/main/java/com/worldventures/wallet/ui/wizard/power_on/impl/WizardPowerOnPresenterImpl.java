package com.worldventures.wallet.ui.wizard.power_on.impl;


import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.wizard.PowerOnAction;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.wizard.WizardCheckCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnPresenter;
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WizardPowerOnPresenterImpl extends WalletPresenterImpl<WizardPowerOnScreen> implements WizardPowerOnPresenter {

   private final WalletNetworkDelegate networkDelegate;
   private final WizardInteractor wizardInteractor;
   private final WalletBluetoothService bluetoothService;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public WizardPowerOnPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate, WalletNetworkDelegate networkDelegate,
         WizardInteractor wizardInteractor, WalletBluetoothService bluetoothService, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.networkDelegate = networkDelegate;
      this.wizardInteractor = wizardInteractor;
      this.bluetoothService = bluetoothService;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WizardPowerOnScreen view) {
      super.attachView(view);
      networkDelegate.setup(view);

      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new PowerOnAction()));

      observeBluetoothAndNetwork();
      observeChecks();
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
      getView().setButtonAction(result.internetIsAvailable() && result.bluetoothIsEnabled());
   }

   @Override
   public void openCheckScreen() {
      getNavigator().goWizardChecks();
   }

   @Override
   public void openUserAgreement() {
      getNavigator().goWizardTerms();
   }

   @Override
   public void onBack() {
      getNavigator().goBack();
   }
}

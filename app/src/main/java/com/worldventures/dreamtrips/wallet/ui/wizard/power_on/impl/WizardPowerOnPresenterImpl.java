package com.worldventures.dreamtrips.wallet.ui.wizard.power_on.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PowerOnAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCheckCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.WizardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.WizardPowerOnScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class WizardPowerOnPresenterImpl extends WalletPresenterImpl<WizardPowerOnScreen> implements WizardPowerOnPresenter {

   private final WizardInteractor wizardInteractor;
   private final WalletBluetoothService bluetoothService;
   private final AnalyticsInteractor analyticsInteractor;

   public WizardPowerOnPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WizardInteractor wizardInteractor, WalletBluetoothService bluetoothService, AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.wizardInteractor = wizardInteractor;
      this.bluetoothService = bluetoothService;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WizardPowerOnScreen view) {
      super.attachView(view);
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new PowerOnAction()));

      observeBluetoothAndNetwork();
      observeChecks();
   }

   private void observeBluetoothAndNetwork() {
      Observable.merge(bluetoothService.observeEnablesState(), getNetworkService().observeConnectedState())
            .compose(bindViewIoToMainComposer())
            .subscribe(b -> wizardInteractor.checksPipe().send(new WizardCheckCommand()));
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

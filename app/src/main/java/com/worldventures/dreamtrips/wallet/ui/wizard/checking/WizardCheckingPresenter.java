package com.worldventures.dreamtrips.wallet.ui.wizard.checking;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCheckCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class WizardCheckingPresenter extends WalletPresenter<WizardCheckingPresenter.Screen, Parcelable> {

   @Inject WizardInteractor wizardInteractor;
   @Inject WalletBluetoothService bluetoothService;
   @Inject WalletNetworkService networkService;
   @Inject Navigator navigator;

   public WizardCheckingPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeBluetoothAndNetwork();
      observeChecks();
      wizardInteractor.checksPipe().send(new WizardCheckCommand());
   }

   void goBack() {
      navigator.goBack();
   }

   void goNext() {
      navigator.single(new WizardTermsPath());
   }

   private void observeBluetoothAndNetwork() {
      Observable.merge(bluetoothService.observeEnablesState(), networkService.observeConnectedState())
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
      Screen view = getView();
      //noinspection all
      view.networkAvailable(result.internetIsAvailable());
      view.bluetoothEnable(result.bluetoothIsEnabled());
      if (!result.bleIsSupported()) {
         view.bluetoothDoesNotSupported();
      }

      view.buttonEnable(result.internetIsAvailable() && result.bluetoothIsEnabled());
   }

   interface Screen extends WalletScreen {

      void networkAvailable(boolean available);

      void bluetoothEnable(boolean enable);

      void bluetoothDoesNotSupported();

      void buttonEnable(boolean enable);
   }
}

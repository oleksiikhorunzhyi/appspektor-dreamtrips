package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.poweron;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
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

public class ForceUpdatePowerOnPresenter extends WalletPresenter<ForceUpdatePowerOnPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   //temporary solution due to new flow navigation
   @Inject WizardInteractor wizardInteractor;
   @Inject WalletBluetoothService bluetoothService;
   @Inject WalletNetworkService networkService;

   private final SmartCard smartCard;
   private final FirmwareUpdateData firmwareUpdateData;

   public ForceUpdatePowerOnPresenter(SmartCard smartCard, FirmwareUpdateData firmwareUpdateData, Context context, Injector injector) {
      super(context, injector);
      this.smartCard = smartCard;
      this.firmwareUpdateData = firmwareUpdateData;
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
      getView().setButtonAction((result.internetIsAvailable() && result.bluetoothIsEnabled()) ? v -> openPairCardScreen() :
            v -> getView().showDialogEnableBleAndInternet());
   }

   public void openPairCardScreen() {
      navigator.single(new ForcePairKeyPath(smartCard, firmwareUpdateData));
   }

   public void onBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void setButtonAction(View.OnClickListener onClickListener);

      void showDialogEnableBleAndInternet();
   }
}

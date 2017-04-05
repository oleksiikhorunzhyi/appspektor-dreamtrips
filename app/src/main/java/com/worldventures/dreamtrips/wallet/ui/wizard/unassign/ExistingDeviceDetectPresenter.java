package com.worldventures.dreamtrips.wallet.ui.wizard.unassign;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPath;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class ExistingDeviceDetectPresenter extends WalletPresenter<ExistingDeviceDetectPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;

   private final String smartCardId;

   public ExistingDeviceDetectPresenter(Context context, Injector injector, String smartCardId) {
      super(context, injector);
      this.smartCardId = smartCardId;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      bindSmartCardId();
      observeReAssignCommand();
   }

   void repair() {
      //noinspection ConstantConditions
      getView().showConfirmDialog(smartCardId);
   }

   void repairConfirmed() {
      wizardInteractor.reAssignCardPipe().send(new ReAssignCardCommand(smartCardId));
   }

   public void goBack() {
      navigator.goBack();
   }

   private void bindSmartCardId() {
      //noinspection ConstantConditions
      getView().setSmartCardId(smartCardId);
   }

   private void observeReAssignCommand() {
      //noinspection ConstantConditions
      wizardInteractor.reAssignCardPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.reAssignCardPipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().<ReAssignCardCommand>provideOperationView())
                  .onSuccess(command -> reAssignSuccess())
                  .create());
   }

   private void reAssignSuccess() {
      navigator.single(new PairKeyPath(ProvisioningMode.NEW_DEVICE, smartCardId));
   }

   public interface Screen extends WalletScreen {

      <T> OperationView<T> provideOperationView();

      void setSmartCardId(String scId);

      void showConfirmDialog(String scId);
   }
}

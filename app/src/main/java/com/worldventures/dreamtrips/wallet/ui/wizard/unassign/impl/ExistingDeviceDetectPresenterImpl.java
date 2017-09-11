package com.worldventures.dreamtrips.wallet.ui.wizard.unassign.impl;


import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.ExistingDeviceDetectPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.ExistingDeviceDetectScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class ExistingDeviceDetectPresenterImpl extends WalletPresenterImpl<ExistingDeviceDetectScreen> implements ExistingDeviceDetectPresenter {

   private final WizardInteractor wizardInteractor;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;

   private String smartCardId;

   public ExistingDeviceDetectPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, deviceConnectionDelegate);
      this.wizardInteractor = wizardInteractor;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
   }

   @Override
   public void attachView(ExistingDeviceDetectScreen view) {
      super.attachView(view);
      smartCardId = getView().getSmartCardId();
      bindSmartCardId();
      observeReAssignCommand();
   }

   @Override
   public void repair() {
      //noinspection ConstantConditions
      getView().showConfirmDialog(smartCardId);
   }

   @Override
   public void retryReAssigning() {
      repairConfirmed();
   }

   @Override
   public void repairConfirmed() {
      wizardInteractor.reAssignCardPipe().send(new ReAssignCardCommand(smartCardId));
   }

   public void goBack() {
      getNavigator().goBack();
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
      getNavigator().goPairKeyExistingDevice(ProvisioningMode.SETUP_NEW_DEVICE, smartCardId);
   }

   @Override
   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }
}

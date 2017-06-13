package com.worldventures.dreamtrips.wallet.ui.wizard.assign;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardAssignUserPresenter extends WalletPresenter<WizardAssignUserPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject RecordInteractor recordInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final WizardAssignDelegate wizardAssignDelegate;

   public WizardAssignUserPresenter(Context context, Injector injector, ProvisioningMode mode) {
      super(context, injector);
      wizardAssignDelegate = WizardAssignDelegate.create(mode, wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, navigator);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeComplete();
      onWizardComplete();
   }

   void onWizardComplete() {
      wizardInteractor.completePipe().send(new WizardCompleteCommand());
   }

   void onWizardCancel() {
      navigator.goBack();
   }

   private void observeComplete() {
      wizardInteractor.completePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> wizardAssignDelegate.onAssignUserSuccess(getView()))
                  .create());
   }

   interface Screen extends WalletScreen {

      OperationView<WizardCompleteCommand> provideOperationView();
   }
}

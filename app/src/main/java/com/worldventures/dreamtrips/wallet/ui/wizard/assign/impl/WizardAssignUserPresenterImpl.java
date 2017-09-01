package com.worldventures.dreamtrips.wallet.ui.wizard.assign.impl;


import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.WizardAssignDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.WizardAssignUserPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.WizardAssignUserScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class WizardAssignUserPresenterImpl extends WalletPresenterImpl<WizardAssignUserScreen> implements WizardAssignUserPresenter {

   private final WizardInteractor wizardInteractor;
   private final RecordInteractor recordInteractor;
   private final AnalyticsInteractor analyticsInteractor;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;

   private WizardAssignDelegate wizardAssignDelegate;

   public WizardAssignUserPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
         AnalyticsInteractor analyticsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, smartCardInteractor, networkService);
      this.wizardInteractor = wizardInteractor;
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
   }

   @Override
   public void attachView(WizardAssignUserScreen view) {
      super.attachView(view);
      this.wizardAssignDelegate = WizardAssignDelegate.create(getView().getProvisionMode(), wizardInteractor,
            recordInteractor, analyticsInteractor, getSmartCardInteractor(), getNavigator());
      observeComplete();
      onWizardComplete();
   }

   @Override
   public void onWizardComplete() {
      wizardInteractor.completePipe().send(new WizardCompleteCommand());
   }

   @Override
   public void onWizardCancel() {
      getNavigator().goBack();
   }

   private void observeComplete() {
      wizardInteractor.completePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> wizardAssignDelegate.onAssignUserSuccess(getView()))
                  .create());
   }

   @Override
   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }
}

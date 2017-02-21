package com.worldventures.dreamtrips.wallet.ui.wizard.finish;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.SetupCompleteAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardAssignUserPresenter extends WalletPresenter<WizardAssignUserPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public WizardAssignUserPresenter(Context context, Injector injector) {
      super(context, injector);
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
                  .onSuccess(command -> {
                     analyticsInteractor.walletAnalyticsCommandPipe()
                           .send(new WalletAnalyticsCommand(new SetupCompleteAction()));

                     navigateToNextScreen();
                  })
                  .create());
   }

   private void navigateToNextScreen() {
      navigator.single(new CardListPath());
   }

   interface Screen extends WalletScreen {

      OperationView<WizardCompleteCommand> provideOperationView();
   }
}

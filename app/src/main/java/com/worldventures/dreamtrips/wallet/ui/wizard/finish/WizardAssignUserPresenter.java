package com.worldventures.dreamtrips.wallet.ui.wizard.finish;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;

import javax.inject.Inject;

public class WizardAssignUserPresenter extends WalletPresenter<WizardAssignUserPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;

   public WizardAssignUserPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeComplete();
      wizardInteractor.completePipe().send(new WizardCompleteCommand());
   }

   private void observeComplete() {
      wizardInteractor.completePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<WizardCompleteCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> navigateToNextScreen())
                  .onFail(ErrorHandler.create(getContext(), wizardCompleteCommand -> goBack()))
                  .wrap());
   }

   private void navigateToNextScreen() {
      navigator.single(new CardListPath());
   }

   private void goBack(){
      navigator.goBack();
   }

   interface Screen extends WalletScreen {
   }
}

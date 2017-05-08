package com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PinWasSetAction;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.WizardAssignUserPath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class WalletPinIsSetPresenter extends WalletPresenter<WalletPinIsSetPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject WizardInteractor wizardInteractor;

   public WalletPinIsSetPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new PinWasSetAction()));
   }

   public void goBack() {
      navigator.goBack();
   }

   void navigateToNextScreen() {
      wizardInteractor.provisioningStatePipe()
            .createObservable(ProvisioningModeCommand.fetchState())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ProvisioningModeCommand>()
               .onSuccess(command -> navigator.go(new WizardAssignUserPath(command.getResult()))));
   }

   public interface Screen extends WalletScreen {

   }
}

package com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import timber.log.Timber;

public class WizardUploadProfilePresenter extends WalletPresenter<WizardUploadProfilePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject WalletFeatureHelper featureHelper;

   public WizardUploadProfilePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeSetupUserSmartCardData();
      fetchSmartCardUserData();
   }

   private void observeSetupUserSmartCardData() {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetupUserData())
                  .onSuccess(command -> onUserSetupSuccess(command.getResult()))
                  .onFail((command, throwable) -> {
                     getView().showRetryDialog();
                     Timber.e(throwable, "");
                  })
                  .create());
   }

   private void fetchSmartCardUserData() {
      smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(this::handleSmartCardUserExisting);
   }

   void retryUpload() {
      fetchSmartCardUserData();
   }

   private void handleSmartCardUserExisting(SmartCardUser smartCardUser) {
      wizardInteractor.setupUserDataPipe().send(new SetupUserDataCommand(smartCardUser));
   }

   private void onUserSetupSuccess(SmartCardUser user) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(
                  user.userPhoto() != null ? PhotoWasSetAction.methodDefault() : PhotoWasSetAction.noPhoto())
            );
      featureHelper.navigateFromSetupUserScreen(navigator, user, true);
   }

   public interface Screen extends WalletScreen {

      OperationView<SetupUserDataCommand> provideOperationSetupUserData();

      void showRetryDialog();
   }
}

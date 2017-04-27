package com.worldventures.dreamtrips.wallet.ui.start;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FetchFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletStartPresenter extends WalletPresenter<WalletStartPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject Navigator navigator;
   @Inject FeatureManager featureManager;

   public WalletStartPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      featureManager.with(Feature.WALLET_PROVISIONING,
            this::onWalletAvailable,
            () -> navigator.single(new WalletProvisioningBlockedPath(), Flow.Direction.REPLACE)
      );
   }

   void retryFetchingCard() {
      smartCardInteractor.fetchAssociatedSmartCard().send(new FetchAssociatedSmartCardCommand());
   }

   void cancelFetchingCard() {
      navigator.goBack();
   }

   private void onWalletAvailable() {
      smartCardInteractor.fetchAssociatedSmartCard()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.fetchAssociatedSmartCard()))
            .compose(bindViewIoToMainComposer())
            .subscribe(
                  OperationActionSubscriber.forView(getView().provideOperationView())
                        .onSuccess(command -> handleResult(command.getResult()))
                        .create()
            );
      smartCardInteractor.fetchAssociatedSmartCard().send(new FetchAssociatedSmartCardCommand());
   }

   private void handleResult(FetchAssociatedSmartCardCommand.AssociatedCard associatedCard) {
      if (associatedCard.exist()) {
         navigator.single(new CardListPath(), Flow.Direction.REPLACE);
      } else {
         fetchFirmwareUpdateData(associatedCard);
      }
   }

   private void fetchFirmwareUpdateData(final FetchAssociatedSmartCardCommand.AssociatedCard associatedCard) {
      firmwareInteractor.fetchFirmwareUpdateDataPipe()
            .createObservable(new FetchFirmwareUpdateData())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FetchFirmwareUpdateData>()
                  .onSuccess(command -> checkFirmwareUpdateData(command.getResult(), associatedCard))
                  .onFail((command, throwable) -> navigateToWizard())
            );
   }

   private void checkFirmwareUpdateData(FetchFirmwareUpdateData.Result result, FetchAssociatedSmartCardCommand.AssociatedCard associatedCard) {
      if (result.isForceUpdateStarted()) {
         final FirmwareUpdateData firmwareUpdateData = result.firmwareUpdateData();
         //noinspection ConstantConditions
         if (firmwareUpdateData.fileDownloaded()) {
            navigator.single(new WalletFirmwareChecksPath(), Flow.Direction.REPLACE);
         } else {
            navigator.single(new WalletNewFirmwareAvailablePath(), Flow.Direction.REPLACE);
         }
      } else {
         navigateToWizard();
      }
   }

   private void navigateToWizard() {
      navigator.single(new WizardWelcomePath(), Flow.Direction.REPLACE);
   }

   public interface Screen extends WalletScreen {

      OperationView<FetchAssociatedSmartCardCommand> provideOperationView();

   }
}

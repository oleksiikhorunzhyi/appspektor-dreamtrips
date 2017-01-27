package com.worldventures.dreamtrips.wallet.ui.start;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FetchFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.install.WalletInstallFirmwarePath;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;

import javax.inject.Inject;

import flow.Flow;
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
            .subscribe(OperationActionStateSubscriberWrapper.<FetchFirmwareUpdateData>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> checkFirmwareUpdateData(command.getResult(), associatedCard))
                  .onFail(ErrorHandler.create(getContext(), command -> navigator.goBack()))
                  .wrap()
            );
   }

   private void checkFirmwareUpdateData(FetchFirmwareUpdateData.Result result, FetchAssociatedSmartCardCommand.AssociatedCard associatedCard) {
      if (result.isForceUpdateStarted()) {
         final FirmwareUpdateData firmwareUpdateData = result.firmwareUpdateData();
         //noinspection ConstantConditions
         if (firmwareUpdateData.fileDownloaded()) {
            navigator.single(new WalletInstallFirmwarePath(), Flow.Direction.REPLACE);
         } else {
            navigator.single(new WalletNewFirmwareAvailablePath(), Flow.Direction.REPLACE);
         }
      } else {
         navigator.single(new WizardWelcomePath(associatedCard.smartCard()), Flow.Direction.REPLACE);
      }
   }

   public interface Screen extends WalletScreen {

      OperationView<FetchAssociatedSmartCardCommand> provideOperationView();

   }
}

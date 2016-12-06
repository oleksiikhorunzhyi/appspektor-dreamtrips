package com.worldventures.dreamtrips.wallet.ui.wizard.associate;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.ScidEnteredAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AvailabilitySmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.DisassociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class ConnectSmartCardPresenter extends WalletPresenter<ConnectSmartCardPresenter.Screen, Parcelable> {

   @Inject WizardInteractor wizardInteractor;
   @Inject Navigator navigator;
   @Inject BackStackDelegate backStackDelegate;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final String barcode;
   private final ConnectSmartCardPath.BarcodeOrigin barcodeOrigin;

   public ConnectSmartCardPresenter(Context context, Injector injector, String barcode,
         ConnectSmartCardPath.BarcodeOrigin barcodeOrigin) {
      super(context, injector);
      this.barcode = barcode;
      this.barcodeOrigin = barcodeOrigin;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      view.provideOperationDelegate().showProgress(null);
      backStackDelegate.setListener(() -> true);
   }

   @Override
   public void detachView(boolean retainInstance) {
      super.detachView(retainInstance);
      backStackDelegate.clearListener();
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeConnectionError();
      observeAssociation();
      observerAvailabilitySmartCard();

      wizardInteractor.availabilitySmartCardCommandActionPipe().send(new AvailabilitySmartCardCommand(barcode));
   }

   private void observerAvailabilitySmartCard() {
      wizardInteractor.availabilitySmartCardCommandActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<AvailabilitySmartCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> wizardInteractor.associateCardUserCommandPipe().send(new AssociateCardUserCommand(barcode)))
                  .onFail(ErrorHandler.<AvailabilitySmartCardCommand>builder(getContext())
                        .defaultAction(command -> goBack())
                        .build())
                  .wrap());
   }

   private void observeAssociation() {
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<CreateAndConnectToCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> smartCardConnected(command.getResult()))
                  .onFail(ErrorHandler.<CreateAndConnectToCardCommand>builder(getContext())
                        .handle(SmartCardConnectException.class, R.string.wallet_smartcard_connection_error)
                        .defaultAction(command -> goBack())
                        .build())
                  .wrap());

      wizardInteractor.associateCardUserCommandPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<AssociateCardUserCommand>forView(getView().provideOperationDelegate())
                  .onFail(ErrorHandler.<AssociateCardUserCommand>builder(getContext())
                        .handle(FormatException.class, R.string.wallet_wizard_bar_code_validation_error)
                        .defaultAction(command -> goBack())
                        .build())
                  .wrap()
            );
   }

   private void observeConnectionError() {
      //un-assign immediately after failed connection
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(bindView())
            .compose(new ActionPipeCacheWiper(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(new ActionStateSubscriber<CreateAndConnectToCardCommand>()
                  .onFail((command, throwable) -> startDisassociate(command.getSmartCardId())));
   }

   private void startDisassociate(String smartCardId) {
      wizardInteractor.disassociatePipe().send(new DisassociateCardUserCommand(smartCardId));
   }

   private void smartCardConnected(SmartCard smartCard) {
      navigator.withoutLast(new WizardWelcomePath(smartCard.smartCardId()));
      trackCardAdded(smartCard.smartCardId());
   }

   private void trackCardAdded(String cid) {
      if (barcodeOrigin == ConnectSmartCardPath.BarcodeOrigin.SCAN) {
         analyticsInteractor.walletAnalyticsCommandPipe()
               .send(new WalletAnalyticsCommand(ScidEnteredAction.forScan(cid)));
      } else if (barcodeOrigin == ConnectSmartCardPath.BarcodeOrigin.MANUAL) {
         analyticsInteractor.walletAnalyticsCommandPipe()
               .send(new WalletAnalyticsCommand(ScidEnteredAction.forManual(cid)));
      }
   }

   private void goBack() {
      navigator.goBack();
   }

   interface Screen extends WalletScreen {
   }
}

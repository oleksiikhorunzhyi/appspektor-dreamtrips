package com.worldventures.dreamtrips.wallet.ui.wizard.associate;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.StringRes;

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
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AvailabilitySmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.DisassociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

public class ConnectSmartCardPresenter extends WalletPresenter<ConnectSmartCardPresenter.Screen, Parcelable> {

   @Inject WizardInteractor wizardInteractor;
   @Inject Navigator navigator;
   @Inject BackStackDelegate backStackDelegate;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject WizardMemoryStorage wizardMemoryStorage;

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

      wizardMemoryStorage.saveBarcode(barcode);
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
      observeAssociation();
      observerAvailabilitySmartCard();

      wizardInteractor.availabilitySmartCardCommandActionPipe().send(new AvailabilitySmartCardCommand(barcode));
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
   }

   private void observerAvailabilitySmartCard() {
      wizardInteractor.availabilitySmartCardCommandActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<AvailabilitySmartCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> wizardInteractor.createAndConnectActionPipe().send(new CreateAndConnectToCardCommand()))
                  .onFail(ErrorHandler.<AvailabilitySmartCardCommand>builder(getContext())
                        .defaultAction(command -> goBack())
                        .build())
                  .wrap());
   }

   private boolean checkBarcode() {
      if (!WalletValidateHelper.validateSCId(barcode)) {
         getView().showError(R.string.wallet_wizard_bar_code_validation_error);
         return false;
      } else {
         return true;
      }
   }

   private void smartCardConnected(SmartCard smartCard) {
      if (checkBarcode()) {
         navigator.withoutLast(new WizardWelcomePath());
         trackCardAdded(smartCard.smartCardId());
      }
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

      void showError(@StringRes int messageId);
   }
}

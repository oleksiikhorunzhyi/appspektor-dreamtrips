package com.worldventures.dreamtrips.wallet.ui.wizard.connect_smartcard;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.DisassociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;

import timber.log.Timber;

public class ConnectSmartCardPresenter extends WalletPresenter<ConnectSmartCardPresenter.Screen, Parcelable> {

   @Inject WizardInteractor wizardInteractor;
   @Inject Navigator navigator;
   @Inject BackStackDelegate backStackDelegate;

   private final String barcode;

   public ConnectSmartCardPresenter(Context context, Injector injector, String barcode) {
      super(context, injector);
      this.barcode = barcode;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      view.provideOperationDelegate().showProgress();
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
      startAssignUser();
   }

   private void startAssignUser() {
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<CreateAndConnectToCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> smartCardCreated(command.getResult()))
                  .onFail(ErrorHandler.create(getContext(), command -> {
                     getView().showPairingErrorDialog();
                     Timber.e("Could not connect to device");
                  }))
                  .wrap());

      wizardInteractor.associateCardUserCommandPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<AssociateCardUserCommand>forView(getView()
                  .provideOperationDelegate())
                  .onFail(ErrorHandler.<AssociateCardUserCommand>builder(getContext())
                        .handle(FormatException.class, R.string.wallet_wizard_bar_code_validation_error)
                        .defaultAction(command -> startDisassociate())
                        .build())
                  .wrap()
            );

      wizardInteractor.associateCardUserCommandPipe().send(new AssociateCardUserCommand(barcode));
   }

   private void startDisassociate() {
      wizardInteractor.disassociateCardUserCommandPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<DisassociateCardUserCommand>forView(getView()
                  .provideOperationDelegate())
                  .onFail(ErrorHandler.<DisassociateCardUserCommand>builder(getContext())
                        .defaultMessage(R.string.wallet_fail_disassociate)
                        .build())
                  .onSuccess(commandDisassociate -> goBack())
                  .wrap());

      wizardInteractor.disassociateCardUserCommandPipe().send(new DisassociateCardUserCommand(barcode));
   }

   private void smartCardCreated(SmartCard smartCard) {
      if (smartCard.connectionStatus().isConnected()) {
         smartCardConnected(smartCard.smartCardId());
      } else {
         getView().showPairingErrorDialog();
      }
   }

   private void smartCardConnected(String smartCardId) {
      navigator.withoutLast(new WizardWelcomePath(smartCardId));
   }

   public void goBack() {
      navigator.goBack();
   }

   interface Screen extends WalletScreen {

      void showPairingErrorDialog();
   }
}

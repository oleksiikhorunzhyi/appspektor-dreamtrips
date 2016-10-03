package com.worldventures.dreamtrips.wallet.ui.wizard.connect_smartcard;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;

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
                  .onSuccess(command -> smartCardCreated(command.getSmartCardId()))
                  .onFail(ErrorHandler.<CreateAndConnectToCardCommand>builder(getContext())
                        .defaultMessage(R.string.wallet_wizard_scid_validation_error)
                        .defaultAction(command ->  {
                           navigator.goBack();
                           Timber.e("Could not connect to device");
                        })
                        .build())
                  .wrap());

      wizardInteractor.associateCardUserCommandPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<AssociateCardUserCommand>forView(getView().provideOperationDelegate())
                  .onFail(ErrorHandler.create(getContext(), command -> navigator.goBack()))
                  .wrap()
            );

      wizardInteractor.associateCardUserCommandPipe().send(new AssociateCardUserCommand(barcode));
   }

   private void smartCardCreated(String smartCardId) {
      navigator.withoutLast(new WizardWelcomePath(smartCardId));
   }

   interface Screen extends WalletScreen {
   }

}

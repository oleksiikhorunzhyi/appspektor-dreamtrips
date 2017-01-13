package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.pairkey;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import javax.inject.Inject;

public class ForcePairKeyPresenter extends WalletPresenter<ForcePairKeyPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;

   public ForcePairKeyPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeCreateAndConnectSmartCard();
   }

   private void observeCreateAndConnectSmartCard() {
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<CreateAndConnectToCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> smartCardConnected())
                  .onFail(ErrorHandler.<CreateAndConnectToCardCommand>builder(getContext())
                        .handle(SmartCardConnectException.class, R.string.wallet_smartcard_connection_error)
                        .defaultAction(command -> goBack())
                        .build())
                  .wrap());
   }

   void tryToPairAndConnectSmartCard() {
      wizardInteractor.createAndConnectActionPipe().send(new CreateAndConnectToCardCommand());
   }

   private void smartCardConnected() {
      navigator.withoutLast(new WalletNewFirmwareAvailablePath());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void showError(@StringRes int messageId);
   }
}

package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.CardConnectedAction;
import com.worldventures.dreamtrips.wallet.analytics.CheckFrontAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePath;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

public class PairKeyPresenter extends WalletPresenter<PairKeyPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public PairKeyPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new CheckFrontAction()));

      observeCreateAndConnectSmartCard();
   }

   private void observeCreateAndConnectSmartCard() {
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

   private void smartCardConnected(SmartCard smartCard) {
      if (checkBarcode(smartCard.smartCardId())) {
         navigator.withoutLast(new WizardEditProfilePath(smartCard));
         analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(smartCard, new CardConnectedAction()));
      }
   }

   public void tryToPairAndConnectSmartCard() {
      wizardInteractor.createAndConnectActionPipe().send(new CreateAndConnectToCardCommand());
   }

   private boolean checkBarcode(String barcode) {
      if (!WalletValidateHelper.validateSCId(barcode)) {
         getView().showError(R.string.wallet_wizard_bar_code_validation_error);
         return false;
      } else {
         return true;
      }
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void showError(@StringRes int messageId);
   }
}

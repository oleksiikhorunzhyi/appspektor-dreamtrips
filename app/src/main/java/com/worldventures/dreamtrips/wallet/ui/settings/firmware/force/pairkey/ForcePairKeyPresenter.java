package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.pairkey;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePath;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

public class ForcePairKeyPresenter extends WalletPresenter<ForcePairKeyPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;

   private final SmartCard smartCard;
   private final FirmwareUpdateData firmwareUpdateData;

   public ForcePairKeyPresenter(SmartCard smartCard, FirmwareUpdateData firmwareUpdateData, Context context, Injector injector) {
      super(context, injector);
      this.smartCard = smartCard;
      this.firmwareUpdateData = firmwareUpdateData;
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

   private void smartCardConnected() {
      if (checkBarcode(smartCard.smartCardId())) {
         navigator.withoutLast(new WalletNewFirmwareAvailablePath(smartCard, firmwareUpdateData));
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

package com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class WalletDisableDefaultCardPresenter extends WalletPresenter<WalletDisableDefaultCardPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;

   public WalletDisableDefaultCardPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observerSmartCard();
      observeDelayChange();
   }

   void goBack() {
      navigator.goBack();
   }

   /**
    * @param delayMinutes 0 - never
    */
   void onTimeSelected(long delayMinutes) {
      smartCardInteractor.disableDefaultCardPipe().send(new SetDisableDefaultCardDelayCommand(delayMinutes));
   }

   private void observerSmartCard() {
      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCardModifier -> getView().selectedTime(smartCardModifier.getResult().disableCardDelay()));
   }

   private void observeDelayChange() {
      smartCardInteractor.disableDefaultCardPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetDisableDefaultCardDelayCommand>forView(getView().provideOperationDelegate())
                  .onFail(ErrorHandler.create(getContext()))
                  .wrap());
   }

   public interface Screen extends WalletScreen {

      void selectedTime(long minutes);
   }
}

package com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
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
    * @param delayMillis 0 - never
    */
   void onTimeSelected(long delayMillis) {
      smartCardInteractor.disableDefaultCardPipe().send(new SetDisableDefaultCardDelayCommand(delayMillis));
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
            .subscribe(OperationSubscriberWrapper.<SetDisableDefaultCardDelayCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> {
                  })
                  .onFail(throwable -> {
                     // TODO: 9/14/16 handle disconnect (depend on PR)
                     String msg = getContext().getString(R.string.error_something_went_wrong);
                     return new OperationSubscriberWrapper.MessageActionHolder<>(msg, null);
                  }).wrap());
   }

   public interface Screen extends WalletScreen {

      void selectedTime(long millis);
   }
}

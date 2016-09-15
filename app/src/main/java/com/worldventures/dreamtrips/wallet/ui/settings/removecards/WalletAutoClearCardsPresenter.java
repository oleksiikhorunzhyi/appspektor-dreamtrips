package com.worldventures.dreamtrips.wallet.ui.settings.removecards;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;

public class WalletAutoClearCardsPresenter extends WalletPresenter<WalletAutoClearCardsPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;

   public WalletAutoClearCardsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observerSmartCard();
      observeDelayChange();
   }

   public void goBack() {
      Flow.get(getContext()).goBack();
   }

   /**
    * @param delayMillis 0 - never
    */
   public void onTimeSelected(long delayMillis) {
      smartCardInteractor.autoClearDelayPipe().send(new SetAutoClearSmartCardDelayCommand(delayMillis));
   }

   private void observerSmartCard() {
      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCardModifier -> getView().selectedTime(smartCardModifier.getResult().clearFlyeDelay()));
   }

   private void observeDelayChange() {
      smartCardInteractor.autoClearDelayPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationSubscriberWrapper.<SetAutoClearSmartCardDelayCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> {})
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

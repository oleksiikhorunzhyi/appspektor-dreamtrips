package com.worldventures.dreamtrips.wallet.ui.wizard.paymentcards;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardSyncManager;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WizardAssignUserPath;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class SyncPaymentCardPresenter extends WalletPresenter<SyncPaymentCardPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardSyncManager smartCardSyncManager;

   private final SmartCard smartCard;

   public SyncPaymentCardPresenter(Context context, Injector injector, SmartCard smartCard) {
      super(context, injector);
      this.smartCard = smartCard;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeSyncPaymentCards();
   }

   // TODO: 2/15/17 Add logic 
   private void observeSyncPaymentCards() {
      // TODO: 2/15/17 observer sync payment cards, show and update progress
      getView().setCountPaymentCardsProgress(1, 1);
      getView().setProgressInPercent(50);

      //test
      Observable.timer(5000, TimeUnit.MILLISECONDS, Schedulers.io())
            .compose(bindViewIoToMainComposer())
            .repeat(3)
            .subscribe(aLong -> goToNext());
   }

   void goToNext() {
      navigator.go(new WizardAssignUserPath(smartCard));
   }

   public interface Screen extends WalletScreen {

      void setCountPaymentCardsProgress(int syncedCardsCount, int allCardsCount);

      void setProgressInPercent(int percent);
   }
}

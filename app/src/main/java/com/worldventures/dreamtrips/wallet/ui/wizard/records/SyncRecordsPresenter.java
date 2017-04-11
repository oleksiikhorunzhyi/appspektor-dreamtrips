package com.worldventures.dreamtrips.wallet.ui.wizard.records;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.SyncPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;

import javax.inject.Inject;

public class SyncRecordsPresenter extends WalletPresenter<SyncRecordsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject RecordInteractor recordInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final SyncRecordDelegate syncRecordDelegate;

   public SyncRecordsPresenter(Context context, Injector injector, SyncAction syncAction) {
      super(context, injector);
      syncRecordDelegate = SyncRecordDelegate.create(syncAction, smartCardInteractor, recordInteractor, navigator);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();

      syncRecordDelegate.bindView(getView());
   }

   void retrySync() {
      syncRecordDelegate.retry();
   }

   void navigateToWallet() {
      navigator.single(new CardListPath());
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new SyncPaymentCardAction()));
   }

   public interface Screen extends WalletScreen, SyncView {
   }
}

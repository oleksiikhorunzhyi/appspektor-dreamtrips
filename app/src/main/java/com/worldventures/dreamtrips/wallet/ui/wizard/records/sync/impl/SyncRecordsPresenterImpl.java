package com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.impl;


import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.SyncPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.SyncRecordDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.SyncRecordsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.SyncRecordsScreen;

public class SyncRecordsPresenterImpl extends WalletPresenterImpl<SyncRecordsScreen> implements SyncRecordsPresenter {

   private final RecordInteractor recordInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   private SyncRecordDelegate syncRecordDelegate;

   public SyncRecordsPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         RecordInteractor recordInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(SyncRecordsScreen view) {
      super.attachView(view);
      trackScreen();
      final SyncAction syncAction = getView().getSyncAction();
      syncRecordDelegate = SyncRecordDelegate.create(syncAction, getSmartCardInteractor(), recordInteractor, getNavigator());
      syncRecordDelegate.bindView(getView());
   }

   @Override
   public void retrySync() {
      syncRecordDelegate.retry();
   }

   @Override
   public void navigateToWallet() {
      getNavigator().goCardList();
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new SyncPaymentCardAction()));
   }
}

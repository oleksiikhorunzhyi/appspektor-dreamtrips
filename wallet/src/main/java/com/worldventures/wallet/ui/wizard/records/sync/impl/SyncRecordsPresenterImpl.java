package com.worldventures.wallet.ui.wizard.records.sync.impl;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.new_smartcard.SyncPaymentCardAction;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.records.SyncAction;
import com.worldventures.wallet.ui.wizard.records.sync.SyncRecordDelegate;
import com.worldventures.wallet.ui.wizard.records.sync.SyncRecordsPresenter;
import com.worldventures.wallet.ui.wizard.records.sync.SyncRecordsScreen;

public class SyncRecordsPresenterImpl extends WalletPresenterImpl<SyncRecordsScreen> implements SyncRecordsPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final RecordInteractor recordInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   private SyncRecordDelegate syncRecordDelegate;

   public SyncRecordsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(SyncRecordsScreen view) {
      super.attachView(view);
      trackScreen();
      final SyncAction syncAction = getView().getSyncAction();
      syncRecordDelegate = SyncRecordDelegate.create(syncAction, smartCardInteractor, recordInteractor, getNavigator());
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

package com.worldventures.wallet.ui.wizard.input.helper;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.wizard.ScidEnteredAction;
import com.worldventures.wallet.analytics.wizard.ScidScannedAction;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;

public abstract class InputAnalyticsDelegate {

   protected final WalletAnalyticsInteractor analyticsInteractor;

   private InputAnalyticsDelegate(WalletAnalyticsInteractor analyticsInteractor) {
      this.analyticsInteractor = analyticsInteractor;
   }

   public static InputAnalyticsDelegate createForScannerScreen(WalletAnalyticsInteractor analyticsInteractor) {
      return new ScannerDelegate(analyticsInteractor);
   }

   public static InputAnalyticsDelegate createForManualInputScreen(WalletAnalyticsInteractor analyticsInteractor) {
      return new ManualInputDelegate(analyticsInteractor);
   }

   public abstract void scannedSuccessfully(String smartCardId);

   private final static class ScannerDelegate extends InputAnalyticsDelegate {

      private ScannerDelegate(WalletAnalyticsInteractor analyticsInteractor) {
         super(analyticsInteractor);
      }

      @Override
      public void scannedSuccessfully(String smartCardId) {
         analyticsInteractor.walletAnalyticsPipe()
               .send(new WalletAnalyticsCommand(new ScidScannedAction(smartCardId)));
      }
   }

   private final static class ManualInputDelegate extends InputAnalyticsDelegate {

      private ManualInputDelegate(WalletAnalyticsInteractor analyticsInteractor) {
         super(analyticsInteractor);
      }

      @Override
      public void scannedSuccessfully(String smartCardId) {
         analyticsInteractor.walletAnalyticsPipe()
               .send(new WalletAnalyticsCommand(new ScidEnteredAction(smartCardId)));
      }
   }

}

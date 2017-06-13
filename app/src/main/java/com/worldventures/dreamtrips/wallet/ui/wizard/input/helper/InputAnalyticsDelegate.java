package com.worldventures.dreamtrips.wallet.ui.wizard.input.helper;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.ScidEnteredAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.ScidScannedAction;

public abstract class InputAnalyticsDelegate {

   protected final AnalyticsInteractor analyticsInteractor;

   private InputAnalyticsDelegate(AnalyticsInteractor analyticsInteractor) {
      this.analyticsInteractor = analyticsInteractor;
   }

   public static InputAnalyticsDelegate createForScannerScreen(AnalyticsInteractor analyticsInteractor) {
      return new ScannerDelegate(analyticsInteractor);
   }

   public static InputAnalyticsDelegate createForManualInputScreen(AnalyticsInteractor analyticsInteractor) {
      return new ManualInputDelegate(analyticsInteractor);
   }

   public abstract void scannedSuccessfully(String smartCardId);

   private static class ScannerDelegate extends InputAnalyticsDelegate {

      private ScannerDelegate(AnalyticsInteractor analyticsInteractor) {
         super(analyticsInteractor);
      }

      @Override
      public void scannedSuccessfully(String smartCardId) {
         analyticsInteractor.walletAnalyticsCommandPipe()
               .send(new WalletAnalyticsCommand(new ScidScannedAction(smartCardId)));
      }
   }

   private static class ManualInputDelegate extends InputAnalyticsDelegate {

      private ManualInputDelegate(AnalyticsInteractor analyticsInteractor) {
         super(analyticsInteractor);
      }

      @Override
      public void scannedSuccessfully(String smartCardId) {
         analyticsInteractor.walletAnalyticsCommandPipe()
               .send(new WalletAnalyticsCommand(new ScidEnteredAction(smartCardId)));
      }
   }

}

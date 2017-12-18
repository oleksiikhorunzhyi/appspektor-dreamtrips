package com.worldventures.wallet.ui.wizard.input.helper

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.ScidEnteredAction
import com.worldventures.wallet.analytics.wizard.ScidScannedAction
import com.worldventures.wallet.service.WalletAnalyticsInteractor

abstract class InputAnalyticsDelegate private constructor(protected val analyticsInteractor: WalletAnalyticsInteractor) {

   abstract fun scannedSuccessfully(smartCardId: String)

   companion object {

      fun createForScannerScreen(analyticsInteractor: WalletAnalyticsInteractor): InputAnalyticsDelegate {
         return ScannerDelegate(analyticsInteractor)
      }

      fun createForManualInputScreen(analyticsInteractor: WalletAnalyticsInteractor): InputAnalyticsDelegate {
         return ManualInputDelegate(analyticsInteractor)
      }

      private class ScannerDelegate(analyticsInteractor: WalletAnalyticsInteractor) : InputAnalyticsDelegate(analyticsInteractor) {

         override fun scannedSuccessfully(smartCardId: String) {
            analyticsInteractor.walletAnalyticsPipe()
                  .send(WalletAnalyticsCommand(ScidScannedAction(smartCardId)))
         }
      }

      private class ManualInputDelegate(analyticsInteractor: WalletAnalyticsInteractor) : InputAnalyticsDelegate(analyticsInteractor) {

         override fun scannedSuccessfully(smartCardId: String) {
            analyticsInteractor.walletAnalyticsPipe()
                  .send(WalletAnalyticsCommand(ScidEnteredAction(smartCardId)))
         }
      }
   }

}

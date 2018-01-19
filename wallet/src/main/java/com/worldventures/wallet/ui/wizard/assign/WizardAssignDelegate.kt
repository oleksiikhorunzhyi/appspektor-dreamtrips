package com.worldventures.wallet.ui.wizard.assign

import com.worldventures.wallet.analytics.WalletAnalyticsAction
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.SetupCompleteAction
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.service.command.ActivateSmartCardCommand
import com.worldventures.wallet.service.command.RecordListCommand
import com.worldventures.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.service.provisioning.ProvisioningModeCommand
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.records.SyncAction
import com.worldventures.wallet.util.WalletFeatureHelper
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

abstract class WizardAssignDelegate private constructor(protected val wizardInteractor: WizardInteractor,
                                                        protected val recordInteractor: RecordInteractor,
                                                        protected val analyticsInteractor: WalletAnalyticsInteractor,
                                                        protected val smartCardInteractor: SmartCardInteractor,
                                                        protected val walletFeatureHelper: WalletFeatureHelper,
                                                        protected val navigator: Navigator) {

   protected abstract fun toNextScreen(view: WizardAssignUserScreen)

   fun onAssignUserSuccess(view: WizardAssignUserScreen) {
      sendAnalytic(SetupCompleteAction())
      wizardInteractor.provisioningStatePipe().send(ProvisioningModeCommand.clear())
      toNextScreen(view)
   }

   private fun sendAnalytic(action: WalletAnalyticsAction) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(WalletAnalyticsCommand(action))
   }

   protected fun activateSmartCard() {
      wizardInteractor.activateSmartCardPipe().send(ActivateSmartCardCommand())
   }

   private class WizardAssignDelegateStandard(wizardInteractor: WizardInteractor,
                                              recordInteractor: RecordInteractor,
                                              analyticsInteractor: WalletAnalyticsInteractor,
                                              smartCardInteractor: SmartCardInteractor,
                                              walletFeatureHelper: WalletFeatureHelper,
                                              navigator: Navigator) : WizardAssignDelegate(
         wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, walletFeatureHelper, navigator) {

      override fun toNextScreen(view: WizardAssignUserScreen) {
         activateSmartCard()
         walletFeatureHelper.finishRegularProvisioning(navigator)
      }
   }

   private class WizardAssignDelegateNewCard(wizardInteractor: WizardInteractor,
                                             recordInteractor: RecordInteractor,
                                             analyticsInteractor: WalletAnalyticsInteractor,
                                             smartCardInteractor: SmartCardInteractor,
                                             walletFeatureHelper: WalletFeatureHelper,
                                             navigator: Navigator) : WizardAssignDelegate(
         wizardInteractor, recordInteractor, analyticsInteractor, smartCardInteractor, walletFeatureHelper, navigator) {

      override fun toNextScreen(view: WizardAssignUserScreen) {
         fetchRecordList(view)
               .subscribe { records -> navigateToNextScreen(!records.isEmpty()) }
      }

      private fun fetchRecordList(view: WizardAssignUserScreen): Observable<List<Record>> {
         return recordInteractor.cardsListPipe()
               .createObservableResult(RecordListCommand.fetch())
               .map { command -> command.result }
               .onErrorReturn { emptyList() }
               .compose(view.bindUntilDetach())
               .observeOn(AndroidSchedulers.mainThread())
      }

      private fun navigateToNextScreen(needToSyncPaymentCards: Boolean) {
         if (needToSyncPaymentCards) {
            navigator.goSyncRecordsPath(SyncAction.TO_CARD)
         } else {
            finishSetupAndNavigateToDashboard()
         }
      }

      private fun finishSetupAndNavigateToDashboard() {
         restoreOfflineModeDefaultState()
         activateSmartCard()
         navigator.goCardList()
      }

      private fun restoreOfflineModeDefaultState() {
         smartCardInteractor.restoreOfflineModeDefaultStatePipe()
               .send(RestoreOfflineModeDefaultStateCommand())
      }
   }

   companion object {

      fun create(mode: ProvisioningMode, wizardInteractor: WizardInteractor, recordInteractor: RecordInteractor,
                 analyticsInteractor: WalletAnalyticsInteractor, smartCardInteractor: SmartCardInteractor,
                 walletFeatureHelper: WalletFeatureHelper, navigator: Navigator): WizardAssignDelegate {
         return if (mode == ProvisioningMode.STANDARD) {
            WizardAssignDelegateStandard(wizardInteractor, recordInteractor, analyticsInteractor,
                  smartCardInteractor, walletFeatureHelper, navigator)
         } else {
            WizardAssignDelegateNewCard(wizardInteractor, recordInteractor, analyticsInteractor,
                  smartCardInteractor, walletFeatureHelper, navigator)
         }
      }
   }
}

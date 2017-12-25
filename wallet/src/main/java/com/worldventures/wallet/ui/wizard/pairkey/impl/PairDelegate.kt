package com.worldventures.wallet.ui.wizard.pairkey.impl

import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.pairkey.PairView
import com.worldventures.wallet.ui.wizard.records.SyncAction
import io.techery.janet.helper.ActionStateSubscriber
import rx.android.schedulers.AndroidSchedulers

internal abstract class PairDelegate private constructor(
      protected val navigator: Navigator,
      protected val smartCardInteractor: SmartCardInteractor) {

   abstract fun prepareView(view: PairView)

   abstract fun cardConnected(view: PairView, smartCardId: String)

   private class NewDeviceDelegate(navigator: Navigator, smartCardInteractor: SmartCardInteractor)
      : PairDelegate(navigator, smartCardInteractor) {

      override fun prepareView(view: PairView) {
         view.hideBackButton()
      }

      override fun cardConnected(view: PairView, smartCardId: String) {
         navigator.goSyncRecordsPath(SyncAction.TO_DEVICE)
      }
   }

   private class SetupDelegate(navigator: Navigator,
                               smartCardInteractor: SmartCardInteractor,
                               private val provisioningMode: ProvisioningMode)
      : PairDelegate(navigator, smartCardInteractor) {

      override fun prepareView(view: PairView) {
         view.showBackButton()
      }

      override fun cardConnected(view: PairView, smartCardId: String) {
         smartCardInteractor.activeSmartCardPipe()
               .createObservableResult(ActiveSmartCardCommand(SmartCard(smartCardId = smartCardId, cardStatus = CardStatus.IN_PROVISIONING)))
               .flatMap { smartCardInteractor.smartCardUserPipe().createObservable(SmartCardUserCommand.fetch()) }
               .compose(view.bindUntilDetach())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(ActionStateSubscriber<SmartCardUserCommand>()
                     .onSuccess { command -> handleSmartCardUserExisting(command.result) }
               )
      }

      private fun handleSmartCardUserExisting(smartCardUser: SmartCardUser?) {
         if (smartCardUser != null) {
            navigator.goWizardUploadProfile(provisioningMode)
         } else {
            navigator.goWizardEditProfile(provisioningMode)
         }
      }
   }

   companion object {

      fun create(mode: ProvisioningMode, navigator: Navigator, smartCardInteractor: SmartCardInteractor): PairDelegate {
         return if (mode == ProvisioningMode.SETUP_NEW_DEVICE) {
            NewDeviceDelegate(navigator, smartCardInteractor)
         } else { // ProvisioningMode.STANDARD or ProvisioningMode.SETUP_NEW_CARD
            SetupDelegate(navigator, smartCardInteractor, mode)
         }
      }
   }
}

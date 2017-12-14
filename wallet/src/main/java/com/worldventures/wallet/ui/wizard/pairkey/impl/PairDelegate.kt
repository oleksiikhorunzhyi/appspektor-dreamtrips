package com.worldventures.wallet.ui.wizard.pairkey.impl

import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.pairkey.PairView
import com.worldventures.wallet.ui.wizard.records.SyncAction

import io.techery.janet.helper.ActionStateSubscriber
import rx.android.schedulers.AndroidSchedulers

internal abstract class PairDelegate private constructor(
      protected val navigator: Navigator,
      protected val smartCardInteractor: SmartCardInteractor,
      internal val provisioningMode: ProvisioningMode) {

   abstract fun prepareView(view: PairView)

   abstract fun navigateOnNextScreen(view: PairView)

   private class NewDeviceDelegate internal constructor(navigator: Navigator,
                                                        smartCardInteractor: SmartCardInteractor,
                                                        provisioningMode: ProvisioningMode)
      : PairDelegate(navigator, smartCardInteractor, provisioningMode) {

      override fun prepareView(view: PairView) {
         view.hideBackButton()
      }

      override fun navigateOnNextScreen(view: PairView) {
         navigator.goSyncRecordsPath(SyncAction.TO_DEVICE)
      }
   }

   private class SetupDelegate internal constructor(navigator: Navigator,
                                                    smartCardInteractor: SmartCardInteractor,
                                                    provisioningMode: ProvisioningMode)
      : PairDelegate(navigator, smartCardInteractor, provisioningMode) {

      override fun prepareView(view: PairView) {
         view.showBackButton()
      }

      override fun navigateOnNextScreen(view: PairView) {
         smartCardInteractor.smartCardUserPipe()
               .createObservable(SmartCardUserCommand.fetch())
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
            NewDeviceDelegate(navigator, smartCardInteractor, mode)
         } else { // ProvisioningMode.STANDARD or ProvisioningMode.SETUP_NEW_CARD
            SetupDelegate(navigator, smartCardInteractor, mode)
         }
      }
   }
}

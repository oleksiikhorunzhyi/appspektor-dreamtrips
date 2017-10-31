package com.worldventures.wallet.ui.wizard.input.helper

import com.worldventures.dreamtrips.api.smart_card.status.model.SmartCardStatus
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.navigation.Navigator
import io.techery.janet.ActionState
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class InputBarcodeDelegateImpl(private val navigator: Navigator,
                               private val wizardInteractor: WizardInteractor,
                               private val analyticsDelegate: InputAnalyticsDelegate,
                               private val smartCardInteractor: SmartCardInteractor) : InputBarcodeDelegate {

   override fun barcodeEntered(barcode: String) {
      fetchCardStatus(barcode)
   }

   override fun retry(barcode: String) {
      fetchCardStatus(barcode)
   }

   private fun fetchCardStatus(barcode: String) {
      wizardInteractor.smartCardStatusCommandActionPipe.send(GetSmartCardStatusCommand(barcode))
   }

   override fun init(inputDelegateView: InputDelegateView) {
      wizardInteractor.smartCardStatusCommandActionPipe
            .observe()
            .compose<ActionState<GetSmartCardStatusCommand>>(inputDelegateView.bindUntilDetach<ActionState<GetSmartCardStatusCommand>>())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(inputDelegateView.provideOperationFetchCardStatus())
                  .onSuccess { command -> handleSmartCardStatus(inputDelegateView, command) }
                  .onFail { _, throwable -> Timber.e(throwable, "") }
                  .create())

      smartCardInteractor.fetchAssociatedSmartCard()
            .observeSuccess()
            .flatMap<ActionState<SmartCardUserCommand>> { _ ->
               smartCardInteractor.fetchAssociatedSmartCard().clearReplays()
               smartCardInteractor.smartCardUserPipe().createObservable(SmartCardUserCommand.fetch())
            }
            .compose<ActionState<SmartCardUserCommand>>(inputDelegateView.bindUntilDetach<ActionState<SmartCardUserCommand>>())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(inputDelegateView.provideOperationFetchSmartCardUser())
                  .onSuccess { command -> handleSmartCardUserExisting(command.result) }
                  .create())
   }

   private fun fetchAssociatedSmartCard() {
      smartCardInteractor.fetchAssociatedSmartCard().send(FetchAssociatedSmartCardCommand())
   }

   override fun retryAssignedToCurrentDevice() {
      fetchAssociatedSmartCard()
   }

   private fun handleSmartCardUserExisting(smartCardUser: SmartCardUser?) {
      if (smartCardUser != null) {
         navigator.goWizardUploadProfile(ProvisioningMode.STANDARD)
      } else {
         navigator.goWizardEditProfile(ProvisioningMode.STANDARD)
      }
   }

   private fun cardAssignToAnotherDevice(smartCardId: String) {
      navigator.goExistingDeviceDetected(smartCardId)
   }

   private fun cardIsUnassigned(smartCardId: String) {
      sendAnalytics(smartCardId)
      navigator.goPairKey(ProvisioningMode.STANDARD, smartCardId)
   }

   private fun sendAnalytics(smartCardId: String) {
      analyticsDelegate.scannedSuccessfully(smartCardId)
   }

   private fun handleSmartCardStatus(inputDelegateView: InputDelegateView, command: GetSmartCardStatusCommand) {
      when (command.result) {
         SmartCardStatus.ASSIGNED_TO_CURRENT_DEVICE -> fetchAssociatedSmartCard()
         SmartCardStatus.UNASSIGNED -> cardIsUnassigned(command.smartCardId)
         SmartCardStatus.ASSIGNED_TO_ANOTHER_DEVICE -> cardAssignToAnotherDevice(command.smartCardId)
         SmartCardStatus.ASSIGNED_TO_ANOTHER_USER -> inputDelegateView.showErrorCardIsAssignedDialog()
         else -> fetchAssociatedSmartCard()
      }
   }
}
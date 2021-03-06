package com.worldventures.wallet.ui.wizard.input.helper

import com.worldventures.dreamtrips.api.smart_card.status.model.SmartCardStatus
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.http.AcceptSmartCardAgreementsCommand
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.service.provisioning.ProvisioningModeCommand
import com.worldventures.wallet.ui.common.base.screen.RxLifecycleView
import com.worldventures.wallet.ui.common.navigation.Navigator
import io.techery.janet.ActionState
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class InputBarcodeDelegateImpl(private val navigator: Navigator,
                               private val wizardInteractor: WizardInteractor,
                               private val analyticsDelegate: InputAnalyticsDelegate,
                               private val smartCardInteractor: SmartCardInteractor) : InputBarcodeDelegate {

   override fun checkBarcode(barcode: String) {
      fetchCardStatus(barcode)
   }

   override fun retry(barcode: String) {
      fetchCardStatus(barcode)
   }

   override fun retryAgreementsAccept(smartCardStatus: SmartCardStatus, smartCardId: String) {
      acceptSmartCardAgreements(smartCardStatus, smartCardId)
   }

   override fun handleAcceptanceCancelled() {
      navigator.returnWalletStart()
   }

   private fun acceptSmartCardAgreements(smartCardStatus: SmartCardStatus, smartCardId: String) {
      wizardInteractor.acceptSmartCardAgreementsPipe().send(AcceptSmartCardAgreementsCommand(smartCardId, smartCardStatus))
   }

   private fun fetchCardStatus(barcode: String) {
      wizardInteractor.smartCardStatusCommandActionPipe.send(GetSmartCardStatusCommand(barcode))
   }

   override fun init(inputDelegateView: InputDelegateView) {
      wizardInteractor.smartCardStatusCommandActionPipe
            .observe()
            .compose(inputDelegateView.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(inputDelegateView.provideOperationFetchCardStatus())
                  .onSuccess { command -> handleSmartCardStatus(inputDelegateView, command) }
                  .onFail { _, throwable -> Timber.e(throwable) }
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

      wizardInteractor.acceptSmartCardAgreementsPipe()
            .observe()
            .compose(inputDelegateView.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(inputDelegateView.provideOperationAcceptAgreements())
                  .onSuccess { command -> handleAcceptableStatus(inputDelegateView, command.smartCardStatus, command.smartCardId) }
                  .create())
   }

   private fun fetchAssociatedSmartCard() {
      smartCardInteractor.fetchAssociatedSmartCard().send(FetchAssociatedSmartCardCommand(skipLocalData = true))
   }

   override fun retryAssignedToCurrentDevice() {
      fetchAssociatedSmartCard()
   }

   private fun handleSmartCardUserExisting(smartCardUser: SmartCardUser?) {
      if (smartCardUser != null) {
         navigator.goCardList()
      } else {
         navigator.goWizardEditProfile(ProvisioningMode.STANDARD)
      }
   }

   private fun cardAssignToAnotherDevice(smartCardId: String) {
      navigator.goExistingDeviceDetected(smartCardId)
   }

   private fun cardIsUnassigned(rxLifecycleView: RxLifecycleView, smartCardId: String) {
      sendAnalytics(smartCardId)
      wizardInteractor.provisioningStatePipe()
            .createObservableResult(ProvisioningModeCommand.fetchState())
            .compose(rxLifecycleView.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.result }
            .subscribe({ navigator.goPairKey(it, smartCardId) }, { Timber.e(it) })
   }

   private fun sendAnalytics(smartCardId: String) {
      analyticsDelegate.scannedSuccessfully(smartCardId)
   }

   private fun handleSmartCardStatus(inputDelegateView: InputDelegateView, command: GetSmartCardStatusCommand) {
      val status: SmartCardStatus = command.result
      if (status == SmartCardStatus.ASSIGNED_TO_CURRENT_DEVICE
            || status == SmartCardStatus.UNASSIGNED
            || status == SmartCardStatus.ASSIGNED_TO_ANOTHER_DEVICE) {
         acceptSmartCardAgreements(status, command.smartCardId)
      } else when (status) {
         SmartCardStatus.ASSIGNED_TO_ANOTHER_USER -> inputDelegateView.showErrorCardIsAssignedDialog()
         else -> fetchAssociatedSmartCard()
      }
   }

   private fun handleAcceptableStatus(rxLifecycleView: RxLifecycleView, smartCardStatus: SmartCardStatus, smartCardId: String) {
      when (smartCardStatus) {
         SmartCardStatus.ASSIGNED_TO_CURRENT_DEVICE -> fetchAssociatedSmartCard()
         SmartCardStatus.UNASSIGNED -> cardIsUnassigned(rxLifecycleView, smartCardId)
         SmartCardStatus.ASSIGNED_TO_ANOTHER_DEVICE -> cardAssignToAnotherDevice(smartCardId)
         else -> throw IllegalArgumentException("SmartCard status cannot differ from defined above at this point")
      }
   }
}

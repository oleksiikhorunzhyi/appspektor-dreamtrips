package com.worldventures.dreamtrips.wallet.ui.wizard.input.helper

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.api.smart_card.status.model.SmartCardStatus
import com.worldventures.dreamtrips.wallet.service.WizardInteractor
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode
import com.worldventures.dreamtrips.wallet.ui.common.BaseTest
import com.worldventures.dreamtrips.wallet.ui.common.InteractorBuilder
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator
import io.techery.janet.command.test.Contract
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.ProgressView
import org.junit.Before
import org.junit.Test

class TestInputBarcodeDelegate : BaseTest() {

   private val contractGetSmartCardStatus = Contract.of(GetSmartCardStatusCommand::class.java)

   private val interactorBuilder = InteractorBuilder.configJanet {
      addMockCommandActionService {
         addContract(contractGetSmartCardStatus)
      }
   }

   lateinit var navigator: Navigator
   lateinit var delegate: InputBarcodeDelegate
   lateinit var view: InputDelegateView

   @Before
   fun beforeTests() {
      view = mockScreen(InputDelegateView::class.java)
      val progressView: ProgressView<GetSmartCardStatusCommand> = mock()
      whenever(view.provideOperationFetchCardStatus()).thenReturn(ComposableOperationView<GetSmartCardStatusCommand>(progressView))

      navigator = mock()
      val wizardInteractor = interactorBuilder.createInteractor(WizardInteractor::class)
      val analyticsDelegate: InputAnalyticsDelegate = mock()
      delegate = InputBarcodeDelegate(navigator, wizardInteractor, analyticsDelegate)
      delegate.init(view)
   }

   @Test
   fun testAssignedToAnotherUser() {
      contractGetSmartCardStatus.result(SmartCardStatus.ASSIGNED_TO_ANOTHER_USER)
      delegate.barcodeEntered("00000044")

      verify(view, times(1)).showErrorCardIsAssignedDialog()
      verify(navigator, times(0)).goExistingDeviceDetected(any())
   }

   @Test
   fun testAssignedToAnotherDevice() {
      contractGetSmartCardStatus.result(SmartCardStatus.ASSIGNED_TO_ANOTHER_DEVICE)
      delegate.barcodeEntered("00000044")

      verify(view, times(0)).showErrorCardIsAssignedDialog()
      verify(navigator, times(1)).goExistingDeviceDetected(anyOrNull()) // we have a problem with command
   }

   @Test
   fun testAssignedToCurrentDevice() {
      contractGetSmartCardStatus.result(SmartCardStatus.ASSIGNED_TO_CURRENT_DEVICE)
      delegate.barcodeEntered("00000044")

      verify(view, times(0)).showErrorCardIsAssignedDialog()
      verify(navigator, times(0)).goExistingDeviceDetected(anyOrNull())
      verify(navigator, times(1)).goPairKey(eq(ProvisioningMode.STANDARD), anyOrNull())
   }

   @Test
   fun testUnassigned() {
      contractGetSmartCardStatus.result(SmartCardStatus.UNASSIGNED)
      delegate.barcodeEntered("00000044")

      verify(view, times(0)).showErrorCardIsAssignedDialog()
      verify(navigator, times(0)).goExistingDeviceDetected(anyOrNull())
      verify(navigator, times(1)).goPairKey(eq(ProvisioningMode.STANDARD), anyOrNull())
   }
}
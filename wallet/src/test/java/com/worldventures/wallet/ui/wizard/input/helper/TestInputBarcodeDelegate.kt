package com.worldventures.wallet.ui.wizard.input.helper

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.api.smart_card.status.model.SmartCardStatus
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.model.createTestSmartCard
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.http.AcceptSmartCardAgreementsCommand
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.BaseTest
import com.worldventures.wallet.ui.common.InteractorBuilder
import com.worldventures.wallet.ui.common.navigation.Navigator
import io.techery.janet.command.test.Contract
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.ProgressView
import org.junit.Before
import org.junit.Test

class TestInputBarcodeDelegate : BaseTest() {

   private val contractGetSmartCardStatus = Contract.of(GetSmartCardStatusCommand::class.java)
   private val contractSmartCardUser = Contract.of(SmartCardUserCommand::class.java).result(SmartCardUser("First Name"))
   private val contractFetchAssociatedSmartCard = Contract.of(FetchAssociatedSmartCardCommand::class.java)
         .result(FetchAssociatedSmartCardCommand.AssociatedCard(
               smartCard = createTestSmartCard("55")))

   private val interactorBuilder = InteractorBuilder.configJanet {
      addMockCommandActionService {
         addContract(contractGetSmartCardStatus)
         addContract(contractSmartCardUser)
         addContract(contractFetchAssociatedSmartCard)
         addContract(Contract.of(AcceptSmartCardAgreementsCommand::class.java).result(null))
      }
   }

   lateinit var navigator: Navigator
   lateinit var delegate: InputBarcodeDelegate
   lateinit var view: InputDelegateView

   @Before
   fun beforeTests() {
      view = mockScreen(InputDelegateView::class.java)
      val progressView: ProgressView<GetSmartCardStatusCommand> = mock()
      val progressSmartCardUserView: ProgressView<SmartCardUserCommand> = mock()
      val progressAcceptTermsView: ProgressView<AcceptSmartCardAgreementsCommand> = mock()
      whenever(view.provideOperationFetchCardStatus()).thenReturn(ComposableOperationView<GetSmartCardStatusCommand>(progressView))
      whenever(view.provideOperationFetchSmartCardUser()).thenReturn(ComposableOperationView<SmartCardUserCommand>(progressSmartCardUserView))
      whenever(view.provideOperationAcceptAgreements()).thenReturn(ComposableOperationView<AcceptSmartCardAgreementsCommand>(progressAcceptTermsView))

      navigator = mock()
      val wizardInteractor = interactorBuilder.createInteractor(WizardInteractor::class)
      val smartCardInteractor = interactorBuilder.createInteractor(SmartCardInteractor::class)
      val analyticsDelegate: InputAnalyticsDelegate = mock()
      delegate = InputBarcodeDelegateImpl(navigator, wizardInteractor, analyticsDelegate, smartCardInteractor)
      delegate.init(view)
   }

   @Test
   fun testAssignedToAnotherUser() {
      contractGetSmartCardStatus.result(SmartCardStatus.ASSIGNED_TO_ANOTHER_USER)
      delegate.checkBarcode("00000044")

      verify(view, times(1)).showErrorCardIsAssignedDialog()
      verify(navigator, times(0)).goExistingDeviceDetected(any())
   }

   @Test
   fun testAssignedToAnotherDevice() {
      contractGetSmartCardStatus.result(SmartCardStatus.ASSIGNED_TO_ANOTHER_DEVICE)
      delegate.checkBarcode("00000044")

      verify(view, times(0)).showErrorCardIsAssignedDialog()
      verify(navigator, times(1)).goExistingDeviceDetected(anyOrNull()) // we have a problem with command
   }

   @Test
   fun testAssignedToCurrentDevice() {
      contractGetSmartCardStatus.result(SmartCardStatus.ASSIGNED_TO_CURRENT_DEVICE)
      delegate.checkBarcode("00000044")

      verify(view, times(0)).showErrorCardIsAssignedDialog()
      verify(navigator, times(0)).goExistingDeviceDetected(anyOrNull())
      verify(navigator, times(0)).goWizardEditProfile(eq(ProvisioningMode.STANDARD))
      verify(navigator, times(1)).goCardList()
   }

   @Test
   fun testUnassigned() {
      contractGetSmartCardStatus.result(SmartCardStatus.UNASSIGNED)
      delegate.checkBarcode("00000044")

      verify(view, times(0)).showErrorCardIsAssignedDialog()
      verify(navigator, times(0)).goExistingDeviceDetected(anyOrNull())
      verify(navigator, times(1)).goPairKey(eq(ProvisioningMode.STANDARD), anyOrNull())
   }
}

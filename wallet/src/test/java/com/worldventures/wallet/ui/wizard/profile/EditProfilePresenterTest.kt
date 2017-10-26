package com.worldventures.wallet.ui.wizard.profile

import com.nhaarman.mockito_kotlin.*
import com.worldventures.wallet.domain.entity.ImmutableSmartCardUser
import com.worldventures.wallet.service.*
import com.worldventures.wallet.service.command.SetupUserDataCommand
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand
import com.worldventures.wallet.service.command.profile.RetryHttpUploadUpdatingCommand
import com.worldventures.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.BasePresenterTest
import com.worldventures.wallet.ui.common.InteractorBuilder
import com.worldventures.wallet.ui.common.MockDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.ViewPresenterBinder
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel
import com.worldventures.wallet.ui.wizard.profile.impl.WizardEditProfilePresenterImpl
import com.worldventures.wallet.util.FirstNameException
import com.worldventures.wallet.util.LastNameException
import io.techery.janet.command.test.Contract
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.ErrorView
import io.techery.janet.operationsubscriber.view.ProgressView
import org.junit.Test

class EditProfilePresenterTest : BasePresenterTest<WizardEditProfileScreen, WizardEditProfilePresenter>() {

   private val contractGetSmartCardStatus = Contract.of(GetSmartCardStatusCommand::class.java)
   private val contractSetupUserData = Contract.of(SetupUserDataCommand::class.java)
   private val contractRevertSmartCardUserUpdating = Contract.of(RevertSmartCardUserUpdatingCommand::class.java)
   private val contractRetryHttpUploadUpdating = Contract.of(RetryHttpUploadUpdatingCommand::class.java)

   lateinit var screen: WizardEditProfileScreen
   lateinit var presenter: WizardEditProfilePresenter
   lateinit var smartCardInteractor: SmartCardInteractor
   lateinit var smartCardUserDataInteractor: SmartCardUserDataInteractor
   lateinit var setupUserDataErrorView: ErrorView<SetupUserDataCommand>

   private val interactorBuilder = InteractorBuilder.configJanet {
      addMockSmartCardActionService()
      addMockCommandActionService {
         addContract(contractGetSmartCardStatus)
         addContract(contractSetupUserData)
         addContract(contractRevertSmartCardUserUpdating)
         addContract(contractRetryHttpUploadUpdating)
      }
   }

   override fun createViewPresenterBinder(): ViewPresenterBinder<WizardEditProfileScreen, WizardEditProfilePresenter> =
         ViewPresenterBinder(screen, presenter)

   override fun setup() {
      val deviceConnectionDelegate: WalletDeviceConnectionDelegate = MockDeviceConnectionDelegate()
      val socialInfoProvider: WalletSocialInfoProvider = mock()

      val analyticsInteractor = interactorBuilder.createInteractor(WalletAnalyticsInteractor::class)
      val wizardInteractor = interactorBuilder.createInteractor(WizardInteractor::class)

      smartCardInteractor = interactorBuilder.createInteractor(SmartCardInteractor::class)
      smartCardUserDataInteractor = interactorBuilder.createInteractor(SmartCardUserDataInteractor::class)

      screen = mockScreen(WizardEditProfileScreen::class.java)

      val progressView: ProgressView<SetupUserDataCommand> = mock()
      setupUserDataErrorView = mock()
      whenever(screen.provideOperationView()).thenReturn(ComposableOperationView<SetupUserDataCommand>(progressView, setupUserDataErrorView))
      whenever(screen.profile).thenReturn(ProfileViewModel())
      whenever(screen.provisionMode).thenReturn(ProvisioningMode.STANDARD)

      presenter = WizardEditProfilePresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            wizardInteractor, analyticsInteractor, socialInfoProvider, smartCardUserDataInteractor)
   }

   @Test
   fun testSaveUserWithoutFirstName() {
      val profile = ProfileViewModel()
      profile.firstName = ""
      profile.lastName = "Test Last"

      whenever(screen.profile).thenReturn(profile)

      presenter.setupUserData()

      verify(setupUserDataErrorView, times(1)).showError(anyOrNull(), any<FirstNameException>())
   }

   @Test
   fun testSaveUserWithoutLastName() {
      val profile = ProfileViewModel()
      profile.firstName = "TestFirst"
      profile.lastName = ""

      whenever(screen.profile).thenReturn(profile)

      presenter.setupUserData()

      verify(setupUserDataErrorView, times(1)).showError(anyOrNull(), any<LastNameException>())
   }

   @Test
   fun testSaveUserCorrectUser() {
      contractSetupUserData.result(ImmutableSmartCardUser.builder()
            .firstName("TestFirst")
            .lastName("Test Last")
            .phoneNumber(null)
            .build())

      val profile = ProfileViewModel()
      profile.firstName = "TestFirst"
      profile.lastName = "Test Last"

      whenever(screen.profile).thenReturn(profile)

      presenter.setupUserData()

      verify(setupUserDataErrorView, times(0)).showError(anyOrNull(), any())
      verify(screen, times(1)).showConfirmationDialog(any())

      presenter.onUserDataConfirmed()

      verify(navigator, times(1)).goWizardAssignUser(any())
      verify(screen, times(2)).provisionMode
   }
}
package com.worldventures.dreamtrips.wallet

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareVersions
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard
import com.worldventures.dreamtrips.wallet.model.TestFirmware
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand
import com.worldventures.dreamtrips.wallet.service.command.firmware.PreInstallationCheckCommand
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

class FirmwareInteractorSpec : BaseSpec({

   describe("Firmware update actions") {
      beforeEach {
         mockDb = createMockDb()
         janet = createJanet()
         bluetoothService = createBluetoothService()
         smartCardInteractor = createInteractor(janet)
         val id = "4"
         val smartcard = mockSmartCard(id)
         whenever(mockDb.getActiveSmartCardId()).thenReturn(id)
         whenever(mockDb.getSmartCard(id)).thenReturn(smartcard)
         smartCardInteractor.activeSmartCardPipe().send(ActiveSmartCardCommand(smartCard))
      }

      context("Pre installation checks") {

         it("smart card is disconnected") {
            val testSubscriber = runPreInstallationCommandSubscriber()
            AssertUtil.assertActionSuccess(testSubscriber, { it.result.bluetoothIsEnabled() })
            AssertUtil.assertActionSuccess(testSubscriber, { !it.result.smartCardIsConnected() })
            AssertUtil.assertActionSuccess(testSubscriber, { !it.result.smartCardIsCharged() })
         }

         it("smart card is ready for updates") {
            whenever(smartCard.connectionStatus()).thenReturn(SmartCard.ConnectionStatus.CONNECTED)

            val testSubscriber = runPreInstallationCommandSubscriber()
            AssertUtil.assertActionSuccess(testSubscriber, { it.result.bluetoothIsEnabled() })
            AssertUtil.assertActionSuccess(testSubscriber, { it.result.smartCardIsConnected() })
            AssertUtil.assertActionSuccess(testSubscriber, { it.result.smartCardIsCharged() })
         }

         it("smart card has low battery level") {
            whenever(smartCard.connectionStatus()).thenReturn(SmartCard.ConnectionStatus.CONNECTED)
            whenever(smartCard.batteryLevel()).thenReturn(45)

            val testSubscriber = runPreInstallationCommandSubscriber()
            AssertUtil.assertActionSuccess(testSubscriber, { it.result.bluetoothIsEnabled() })
            AssertUtil.assertActionSuccess(testSubscriber, { it.result.smartCardIsConnected() })
            AssertUtil.assertActionSuccess(testSubscriber, { !it.result.smartCardIsCharged() })
         }

         it("bluetooth is disable") {
            bluetoothService = createBluetoothService(enable = false)

            val testSubscriber = runPreInstallationCommandSubscriber()
            AssertUtil.assertActionSuccess(testSubscriber, { !it.result.bluetoothIsEnabled() })
            AssertUtil.assertActionSuccess(testSubscriber, { !it.result.smartCardIsConnected() })
            AssertUtil.assertActionSuccess(testSubscriber, { !it.result.smartCardIsCharged() })

         }

      }
   }
}) {
   companion object {

      val smartCard = mockSmartCard("4")

      lateinit var mockDb: SnappyRepository
      lateinit var janet: Janet
      lateinit var smartCardInteractor: SmartCardInteractor
      lateinit var bluetoothService: WalletBluetoothService

      fun createMockDb(): SnappyRepository = spy()

      fun createInteractor(janet: Janet) = SmartCardInteractor(janet, SessionActionPipeCreator(janet), { Schedulers.immediate() })

      fun createFirmwareInteractor(janet: Janet) = FirmwareInteractor(janet)

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .wrapDagger()

         janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })
         daggerCommandActionService.registerProvider(WalletBluetoothService::class.java, { bluetoothService })

         whenever(mockDb.activeSmartCardId).thenReturn("id")
         whenever(mockDb.getSmartCard("id")).thenReturn(smartCard)


         return janet
      }

      fun createBluetoothService(enable: Boolean = true, supported: Boolean = true): WalletBluetoothService {
         return object : WalletBluetoothService {
            override fun isSupported(): Boolean = supported

            override fun isEnable(): Boolean = enable and supported

            override fun observeEnablesState(): Observable<Boolean> = Observable.empty()

         }
      }

      fun mockSmartCard(id: String): SmartCard {
         val mockedSmartCard: SmartCard = mock()
         whenever(mockedSmartCard.smartCardId()).thenReturn(id)
         whenever(mockedSmartCard.batteryLevel()).thenReturn(90)
         whenever(mockedSmartCard.cardStatus()).thenReturn(SmartCard.CardStatus.ACTIVE)
         whenever(mockedSmartCard.connectionStatus()).thenReturn(SmartCard.ConnectionStatus.DISCONNECTED)
         whenever(mockedSmartCard.cardName()).thenReturn("device name")
         whenever(mockedSmartCard.deviceAddress()).thenReturn("device address")
         whenever(mockedSmartCard.cardName()).thenReturn("card name")
         whenever(mockedSmartCard.sdkVersion()).thenReturn("1.0.0")
         whenever(mockedSmartCard.firmwareVersion()).thenReturn(TestFirmware())
         whenever(mockedSmartCard.serialNumber()).thenReturn("")
         return mockedSmartCard
      }

      fun mockFirmwareInfo(): FirmwareInfo {
         val mockFirmwareVersions: FirmwareVersions = mock()
         whenever(mockFirmwareVersions.atmelVersion()).thenReturn("1.0.0")
         whenever(mockFirmwareVersions.bootloaderNordicVersion()).thenReturn("1.0.0")
         whenever(mockFirmwareVersions.nordicVersion()).thenReturn("1.0.0")
         whenever(mockFirmwareVersions.puckAtmelVerstion()).thenReturn("1.0.0")

         val mockFirmware: FirmwareInfo = mock()
         whenever(mockFirmware.id()).thenReturn("30")
         whenever(mockFirmware.firmwareName()).thenReturn("test")
         whenever(mockFirmware.firmwareVersion()).thenReturn("test")
         whenever(mockFirmware.sdkVersion()).thenReturn("1.0.6")
         whenever(mockFirmware.fileSize()).thenReturn(20000)
         whenever(mockFirmware.url()).thenReturn("device addf, vress")
         whenever(mockFirmware.releaseNotes()).thenReturn("card name")
         whenever(mockFirmware.sdkVersion()).thenReturn("1.0.0")
         whenever(mockFirmware.firmwareVersions()).thenReturn(mockFirmwareVersions)

         return mockFirmware
      }

      fun runPreInstallationCommandSubscriber(): TestSubscriber<ActionState<PreInstallationCheckCommand>> {
         val testSubscriber: TestSubscriber<ActionState<PreInstallationCheckCommand>> = TestSubscriber()
         janet.createPipe(PreInstallationCheckCommand::class.java)
               .createObservable(PreInstallationCheckCommand(mockFirmwareInfo()))
               .subscribe(testSubscriber)
         return testSubscriber
      }
   }
}

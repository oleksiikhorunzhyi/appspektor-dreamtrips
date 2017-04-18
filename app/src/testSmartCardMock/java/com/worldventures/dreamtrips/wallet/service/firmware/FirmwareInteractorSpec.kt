package com.worldventures.dreamtrips.wallet.service.firmware

import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.wallet.model.TestFirmware
import com.worldventures.dreamtrips.wallet.model.TestFirmwareUpdateData
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.client.SmartCardClient
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import io.techery.janet.smartcard.mock.device.DeviceStorage
import io.techery.janet.smartcard.mock.device.SimpleDeviceStorage
import io.techery.janet.smartcard.model.ImmutableConnectionParams
import rx.Observable

class FirmwareInteractorSpec : BaseSpec({

   describe("Firmware update actions") {
      beforeEach {
         janet = createJanet()
         bluetoothService = createBluetoothService()
         janet.connectToSmartCardSdk()

         deviceStore.batteryLevel = "100"
      }


   }
}) {
   companion object {
      lateinit var janet: Janet
      lateinit var bluetoothService: WalletBluetoothService
      val firmwareRepository: FirmwareRepository
      val deviceStore: DeviceStorage = SimpleDeviceStorage()
      val cardClient: SmartCardClient

      init {
         firmwareRepository = mockFirmwareRepository("4")
         cardClient = MockSmartCardClient(MockSmartCardClient.DeviceStorageProvider { deviceStore })
      }

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .wrapDagger()

         janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(SmartCardActionService.createDefault(cardClient))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(WalletBluetoothService::class.java, { bluetoothService })
         daggerCommandActionService.registerProvider(FirmwareRepository::class.java, { firmwareRepository })

         return janet
      }

      fun createBluetoothService(enable: Boolean = true, supported: Boolean = true): WalletBluetoothService {
         return object : WalletBluetoothService {
            override fun isSupported(): Boolean = supported

            override fun isEnable(): Boolean = enable and supported

            override fun observeEnablesState(): Observable<Boolean> = Observable.empty()

         }
      }

      fun mockFirmwareRepository(scId: String): FirmwareRepository {
         return TestFirmwareRepositry(
               TestFirmwareUpdateData(scId, true, false, false, TestFirmware())
         )
      }

      fun Janet.connectToSmartCardSdk() {
         this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction(ImmutableConnectionParams.of(1)))
               .toBlocking()
               .subscribe()
      }
   }
}

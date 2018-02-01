package com.worldventures.wallet.service.lostcard

import android.content.Context
import android.location.Location
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.modules.settings.service.SettingsInteractor
import com.worldventures.core.test.AssertUtil
import com.worldventures.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardCoordinates
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType
import com.worldventures.wallet.BaseSpec
import com.worldventures.wallet.domain.converter.ApiPlaceToWalletPlaceConverter
import com.worldventures.wallet.domain.converter.HttpAddressToWalletAddressConverter
import com.worldventures.wallet.domain.converter.SmartCardCoordinatesToWalletCoordinatesConverter
import com.worldventures.wallet.domain.converter.SmartCardLocationToWalletLocationConverter
import com.worldventures.wallet.domain.converter.SmartCardLocationTypeToWalletLocationTypeConverter
import com.worldventures.wallet.domain.converter.WalletCoordinatesToSmartCardCoordinatesConverter
import com.worldventures.wallet.domain.converter.WalletLocationToSmartCardLocationConverter
import com.worldventures.wallet.domain.converter.WalletLocationTypeToSmartCardLocationTypeConverter
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType
import com.worldventures.wallet.domain.entity.lostcard.WalletPlace
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.domain.storage.action.SmartCardActionStorage
import com.worldventures.wallet.model.createTestSmartCard
import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.SmartCardSyncManager
import com.worldventures.wallet.service.TestSchedulerProvider
import com.worldventures.wallet.service.beacon.StubWalletBeaconLogger
import com.worldventures.wallet.service.beacon.WalletBeaconLogger
import com.worldventures.core.service.location.MockDetectLocationService
import com.worldventures.core.service.location.DetectLocationService
import com.worldventures.wallet.service.lostcard.command.DetectGeoLocationCommand
import com.worldventures.wallet.service.lostcard.command.FetchAddressWithPlacesCommand
import com.worldventures.wallet.service.lostcard.command.FetchTrackingStatusCommand
import com.worldventures.wallet.service.lostcard.command.GetLocationCommand
import com.worldventures.wallet.service.lostcard.command.PostLocationCommand
import com.worldventures.wallet.service.lostcard.command.UpdateTrackingStatusCommand
import com.worldventures.wallet.service.lostcard.command.WalletLocationCommand
import com.worldventures.wallet.service.lostcard.command.http.model.AddressRestResponse
import com.worldventures.wallet.service.lostcard.command.http.model.ApiPlace
import com.worldventures.wallet.service.lostcard.command.http.model.NearbyResponse
import com.worldventures.wallet.util.WalletFeatureHelper
import com.worldventures.wallet.util.WalletFeatureHelperFull
import com.worldventures.wallet.util.WalletLocationsUtil
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.client.SmartCardClient
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import io.techery.janet.smartcard.mock.device.DeviceStorage
import io.techery.janet.smartcard.mock.device.SimpleDeviceStorage
import io.techery.janet.smartcard.model.ImmutableConnectionParams
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers.anyList
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.util.Calendar
import java.util.regex.Pattern
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SmartCardLocationInteractorSpec : BaseSpec({

   describe("Lost SmartCard actions") {
      beforeEachTest {
         mockDb = createMockDb()
         janet = createJanet()
         mappery = createMappery()

         smartCardInteractor = createSmartCardInteractor(janet)
         smartCardLocationInteractor = createSmartCardLocationInteractor(janet)
         settingsInteractor = createSettingsInteractor(janet)
         firmwareInteractor = createFirmwareInteractor(janet)
         recordInteractor = createRecordInteractor(janet)
         smartCardSyncManager = createSmartCardSyncManager(janet, smartCardInteractor, firmwareInteractor, recordInteractor)
         detectLocationService = createWalletDetectLocationService()
         locationStorage = mockLostCardStorage()

         janet.connectToSmartCardSdk()
      }

      context("SmartCard Location interactor spec tests") {
         it("take location and save to DB") {
            val smartCard: SmartCard = createTestSmartCard(SMART_CARD_ID)
            whenever(mockDb.smartCard).thenReturn(smartCard)
            val initialLocationsSize = locationStorage.walletLocations.size
            val testSubscriber: TestSubscriber<ActionState<WalletLocationCommand>> = TestSubscriber()

            smartCardLocationInteractor.walletLocationCommandPipe()
                  .createObservable(WalletLocationCommand(WalletLocationType.CONNECT))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { true })
            verify(locationStorage, times(1)).saveWalletLocations(any())
            assert(locationStorage.walletLocations.size == initialLocationsSize + 1)
         }
         it("post location") {
            val smartCard: SmartCard = createTestSmartCard(SMART_CARD_ID)
            val walletLocation = mockWalletLocation()
            var locationList: List<WalletLocation> = mutableListOf()
            whenever(mockDb.smartCard).thenReturn(smartCard)
            whenever(locationStorage.walletLocations).thenReturn(mutableListOf(walletLocation))
            whenever(locationStorage.saveWalletLocations(anyList<WalletLocation>())).thenAnswer({
               locationList = it.arguments[0] as List<WalletLocation>
               return@thenAnswer null
            })

            val testSubscriber: TestSubscriber<ActionState<PostLocationCommand>> = TestSubscriber()
            smartCardLocationInteractor.postLocationPipe()
                  .createObservable(PostLocationCommand())
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { true })
            verify(locationStorage, times(1)).saveWalletLocations(any())
            assert(locationStorage.walletLocations.size == 1)
            assertNotNull(WalletLocationsUtil.getLatestLocation(locationList)?.postedAt)
         }
         it("get location") {
            val smartCard: SmartCard = createTestSmartCard(SMART_CARD_ID)
            whenever(mockDb.smartCard).thenReturn(smartCard)
            whenever(mockDb.walletLocations).thenReturn(mutableListOf<WalletLocation>())

            val testSubscriber: TestSubscriber<ActionState<GetLocationCommand>> = TestSubscriber()
            smartCardLocationInteractor.locationPipe
                  .createObservable(GetLocationCommand())
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { true })
         }
         it("detect geo location") {
            val testSubscriber: TestSubscriber<ActionState<DetectGeoLocationCommand>> = TestSubscriber()
            smartCardLocationInteractor.detectGeoLocationPipe()
                  .createObservable(DetectGeoLocationCommand())
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { it.result != null && it.result.lat > 0f && it.result.lng > 0f })
         }
         it("fetch address by lat lng") {
            val testSubscriber: TestSubscriber<ActionState<FetchAddressWithPlacesCommand>> = TestSubscriber()
            smartCardLocationInteractor.fetchAddressPipe()
                  .createObservable(FetchAddressWithPlacesCommand(createWalletCoordinates()))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { it.result != null })
         }
         it("try to change status on -> off and get toggle state") {
            testTrackingStatusCommand(true, false)
         }
         it("try to change status off -> on and get toggle state") {
            testTrackingStatusCommand(false, true)
         }
      }
   }
}) {
   companion object {
      lateinit var janet: Janet
      lateinit var mockDb: WalletStorage
      lateinit var mappery: MapperyContext
      lateinit var smartCardLocationInteractor: SmartCardLocationInteractor
      lateinit var detectLocationService: DetectLocationService
      lateinit var smartCardInteractor: SmartCardInteractor
      lateinit var firmwareInteractor: FirmwareInteractor
      lateinit var recordInteractor: RecordInteractor
      lateinit var smartCardSyncManager: SmartCardSyncManager
      lateinit var locationStorage: LostCardRepository
      lateinit var settingsInteractor: SettingsInteractor
      private val featureHelper: WalletFeatureHelper = WalletFeatureHelperFull()

      private val deviceStore: DeviceStorage = SimpleDeviceStorage()
      private val cardClient: SmartCardClient

      private val TEST_LAT = 50.448479
      private val TEST_LNG = 30.527267
      val SMART_CARD_ID = "111"

      init {
         cardClient = MockSmartCardClient(MockSmartCardClient.DeviceStorageProvider { deviceStore })
      }

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOf(SmartCardActionStorage(mockDb)))
               .wrapDagger()

         janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService().wrapStub().wrapCache())
               .addService(SmartCardActionService.createDefault(cardClient))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(WalletStorage::class.java) { mockDb }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(SmartCardLocationInteractor::class.java) { smartCardLocationInteractor }
         daggerCommandActionService.registerProvider(DetectLocationService::class.java) { detectLocationService }
         daggerCommandActionService.registerProvider(LostCardRepository::class.java) { locationStorage }
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })
         daggerCommandActionService.registerProvider(FirmwareInteractor::class.java, { firmwareInteractor })
         daggerCommandActionService.registerProvider(RecordInteractor::class.java, { recordInteractor })
         daggerCommandActionService.registerProvider(SettingsInteractor::class.java, { settingsInteractor })
         daggerCommandActionService.registerProvider(WalletBeaconLogger::class.java) { StubWalletBeaconLogger() }

         return janet
      }

      fun createMappery(): MapperyContext = Mappery.Builder()
            .map(SmartCardLocation::class.java).to(WalletLocation::class.java, SmartCardLocationToWalletLocationConverter())
            .map(WalletLocation::class.java).to(SmartCardLocation::class.java, WalletLocationToSmartCardLocationConverter())
            .map(SmartCardCoordinates::class.java).to(WalletCoordinates::class.java, SmartCardCoordinatesToWalletCoordinatesConverter())
            .map(WalletCoordinates::class.java).to(SmartCardCoordinates::class.java, WalletCoordinatesToSmartCardCoordinatesConverter())
            .map(SmartCardLocationType::class.java).to(WalletLocationType::class.java, SmartCardLocationTypeToWalletLocationTypeConverter())
            .map(WalletLocationType::class.java).to(SmartCardLocationType::class.java, WalletLocationTypeToSmartCardLocationTypeConverter())
            .map(ApiPlace::class.java).to(WalletPlace::class.java, ApiPlaceToWalletPlaceConverter())
            .map(AddressRestResponse::class.java).to(WalletAddress::class.java, HttpAddressToWalletAddressConverter())
            .build()

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), TestSchedulerProvider())

      fun createFirmwareInteractor(janet: Janet) = FirmwareInteractor(SessionActionPipeCreator(janet))

      fun createSettingsInteractor(janet: Janet) = SettingsInteractor(SessionActionPipeCreator(janet))

      fun createRecordInteractor(janet: Janet) = RecordInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

      fun createSmartCardSyncManager(janet: Janet, smartCardInteractor: SmartCardInteractor, firmwareInteractor: FirmwareInteractor, recordInteractor: RecordInteractor) = SmartCardSyncManager(janet, smartCardInteractor, firmwareInteractor, recordInteractor, featureHelper)

      fun mockLostCardStorage(): LostCardRepository {
         val lostCardRepository: LostCardRepository = mock()
         whenever(lostCardRepository.isEnableTracking).thenReturn(true)
         whenever(lostCardRepository.walletLocations).thenReturn(mutableListOf())
         return lostCardRepository
      }

      private fun mockHttpService(): MockHttpActionService {
         val placesResponse = mockPlacesResponse()
         val addressResponse = mockAddressRestResponse()
         val scLocations: List<SmartCardLocation> = mutableListOf()
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(scLocations)) { request ->
                  Pattern.compile("api/smartcard/provisioning/card_data/[0-9]+/locations").matcher(request.url).find()
               }
               .bind(MockHttpActionService.Response(200)
                     .body(placesResponse)) { request ->
                  request.url.startsWith("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
               }
               .bind(MockHttpActionService.Response(200).body(addressResponse)) { request ->
                  request.url.startsWith("http://maps.googleapis.com/maps/api/geocode/json")
               }
               .bind(MockHttpActionService.Response(204)) { request ->
                  request.url.endsWith("api/user/settings")
               }
               .build()
      }

      private fun mockAddressRestResponse() = AddressRestResponse(results = emptyList(), placeId = null, status = "unknown")

      fun createMockDb(): WalletStorage = spy()

      fun createSmartCardLocationInteractor(janet: Janet) = SmartCardLocationInteractor(SessionActionPipeCreator(janet))

      fun createWalletDetectLocationService(): DetectLocationService = MockDetectLocationService(mockLocation())

      private fun mockLocation(): Location {
         val location: Location = mock()
         whenever(location.latitude).thenReturn(TEST_LAT)
         whenever(location.longitude).thenReturn(TEST_LNG)
         whenever(location.provider).thenReturn("TestProvider")
         return location
      }

      fun mockWalletLocation() =
            WalletLocation(type = WalletLocationType.CONNECT,
                  coordinates = createWalletCoordinates(),
                  createdAt = Calendar.getInstance().time)

      private fun mockPlacesResponse() = NearbyResponse(listOf(mockApiPlace()))

      private fun mockApiPlace() = ApiPlace("placeId", "Test Place Name", "WTF")

      fun Janet.connectToSmartCardSdk() {
         this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction(ImmutableConnectionParams.of(1)))
               .toBlocking()
               .subscribe()
      }

      fun createWalletCoordinates() = WalletCoordinates(lat = TEST_LAT, lng = TEST_LNG)

      fun testTrackingStatusCommand(trackingStatusExpected: Boolean, trackingStatusPrevious: Boolean) {
         var trackingStatusActual = false
         whenever(locationStorage.isEnableTracking).thenReturn(trackingStatusPrevious)
         whenever(locationStorage.saveEnabledTracking(any())).thenAnswer({
            trackingStatusActual = it.arguments[0] as Boolean
            return@thenAnswer null
         })

         val testFetchSubscriber: TestSubscriber<ActionState<FetchTrackingStatusCommand>> = TestSubscriber()
         val testUpdateSubscriber: TestSubscriber<ActionState<UpdateTrackingStatusCommand>> = TestSubscriber()

         smartCardLocationInteractor.updateTrackingStatusPipe()
               .createObservable(UpdateTrackingStatusCommand(trackingStatusExpected))
               .subscribe(testUpdateSubscriber)

         assertActionSuccess(testUpdateSubscriber, { true })
         testUpdateSubscriber.unsubscribe()

         smartCardLocationInteractor.fetchTrackingStatusPipe()
               .createObservable(FetchTrackingStatusCommand())
               .subscribe(testFetchSubscriber)

         assertActionSuccess(testFetchSubscriber, { true })
         assertEquals(trackingStatusExpected, trackingStatusActual)
         verify(locationStorage, times(1)).saveEnabledTracking(any())
         verify(locationStorage, times(1)).isEnableTracking
         testFetchSubscriber.unsubscribe()
      }
   }
}

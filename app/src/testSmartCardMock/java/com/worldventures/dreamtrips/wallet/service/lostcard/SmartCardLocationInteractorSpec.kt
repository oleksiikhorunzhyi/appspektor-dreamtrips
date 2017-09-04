package com.worldventures.dreamtrips.wallet.service.lostcard

import android.content.Context
import android.location.Address
import android.location.Location
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardCoordinates
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.settings.service.SettingsInteractor
import com.worldventures.dreamtrips.wallet.domain.converter.*
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.*
import com.worldventures.dreamtrips.wallet.domain.storage.action.SmartCardActionStorage
import com.worldventures.dreamtrips.wallet.service.*
import com.worldventures.dreamtrips.wallet.service.location.WalletDetectLocationService
import com.worldventures.dreamtrips.wallet.service.lostcard.command.*
import com.worldventures.dreamtrips.wallet.service.lostcard.command.http.model.AddressRestResponse
import com.worldventures.dreamtrips.wallet.service.lostcard.command.http.model.ApiPlace
import com.worldventures.dreamtrips.wallet.service.lostcard.command.http.model.NearbyResponse
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelperFull
import com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil
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
import io.techery.mappery.Converter
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.util.*
import java.util.regex.Pattern
import kotlin.test.CollectionAssertionSession
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.sizeShouldBe

class SmartCardLocationInteractorSpec : BaseSpec({

   describe("Lost SmartCard actions") {
      beforeEachTest {
         mockDb = createMockDb()
         janet = createJanet()

         smartCardInteractor = createSmartCardInteractor(janet)
         smartCardLocationInteractor = createSmartCardLocationInteractor(janet)
         settingsInteractor = createSettingsInteractor(janet)
         firmwareInteractor = createFirmwareInteractor(janet)
         recordInteractor = createRecordInteractor(janet)
         smartCardSyncManager = createSmartCardSyncManager(janet, smartCardInteractor, firmwareInteractor, recordInteractor)
         walletDetectLocationService = createWalletDetectLocationService()
         locationStorage = mockLostCardStorage()

         janet.connectToSmartCardSdk()
      }

      context("SmartCard Location interactor spec tests") {
         it("take location and save to DB") {
            val smartCard: SmartCard = mockSmartCard(SMART_CARD_ID)
            whenever(mockDb.smartCard).thenReturn(smartCard)
            val initialLocationsSize = locationStorage.walletLocations.size
            val testSubscriber: TestSubscriber<ActionState<WalletLocationCommand>> = TestSubscriber()
            smartCardLocationInteractor.walletLocationCommandPipe()
                  .createObservable(WalletLocationCommand(WalletLocationType.CONNECT))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { true })
            verify(locationStorage, times(1)).saveWalletLocations(any())
            CollectionAssertionSession(locationStorage.walletLocations).sizeShouldBe(initialLocationsSize + 1)
         }
         it("post location") {
            val smartCard: SmartCard = mockSmartCard(SMART_CARD_ID)
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
            CollectionAssertionSession(locationStorage.walletLocations).sizeShouldBe(1)
            assertNotNull(WalletLocationsUtil.getLatestLocation(locationList).postedAt())
         }
         it("get location") {
            val smartCard: SmartCard = mockSmartCard(SMART_CARD_ID)
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

            AssertUtil.assertActionSuccess(testSubscriber, { it.result != null && it.result.lat() > 0f && it.result.lng() > 0f })
         }
         it("fetch address by lat lng") {
            val testSubscriber: TestSubscriber<ActionState<FetchAddressWithPlacesCommand>> = TestSubscriber()
            smartCardLocationInteractor.fetchAddressPipe()
                  .createObservable(FetchAddressWithPlacesCommand(createWalletCoordinates()))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { it.result != null && it.result.address != null })
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
      lateinit var mockDb: SnappyRepository
      lateinit var smartCardLocationInteractor: SmartCardLocationInteractor
      lateinit var walletDetectLocationService: WalletDetectLocationService
      lateinit var smartCardInteractor: SmartCardInteractor
      lateinit var firmwareInteractor: FirmwareInteractor
      lateinit var recordInteractor: RecordInteractor
      lateinit var smartCardSyncManager: SmartCardSyncManager
      lateinit var locationStorage: LostCardRepository
      lateinit var settingsInteractor: SettingsInteractor
      val featureHelper: WalletFeatureHelper = WalletFeatureHelperFull()

      val deviceStore: DeviceStorage = SimpleDeviceStorage()
      val cardClient: SmartCardClient

      val TEST_LAT = 50.448479
      val TEST_LNG = 30.527267
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
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { createMappery() }
         daggerCommandActionService.registerProvider(SmartCardLocationInteractor::class.java) { smartCardLocationInteractor }
         daggerCommandActionService.registerProvider(WalletDetectLocationService::class.java) { walletDetectLocationService }
         daggerCommandActionService.registerProvider(LostCardRepository::class.java) { locationStorage }
         daggerCommandActionService.registerProvider(ReactiveLocationProvider::class.java) { mock() }
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })
         daggerCommandActionService.registerProvider(FirmwareInteractor::class.java, { firmwareInteractor })
         daggerCommandActionService.registerProvider(RecordInteractor::class.java, { recordInteractor })
         daggerCommandActionService.registerProvider(SettingsInteractor::class.java, { settingsInteractor })

         return janet
      }

      fun createMappery(): MapperyContext = Mappery.Builder()
            .map(SmartCardLocation::class.java).to(WalletLocation::class.java, SmartCardLocationToWalletLocationConverter())
            .map(WalletLocation::class.java).to(SmartCardLocation::class.java, WalletLocationToSmartCardLocationConverter())
            .map(Address::class.java).to(WalletAddress::class.java, AndroidAddressToWalletAddressConverter())
            .map(SmartCardCoordinates::class.java).to(WalletCoordinates::class.java, SmartCardCoordinatesToWalletCoordinatesConverter())
            .map(WalletCoordinates::class.java).to(SmartCardCoordinates::class.java, WalletCoordinatesToSmartCardCoordinatesConverter())
            .map(SmartCardLocationType::class.java).to(WalletLocationType::class.java, SmartCardLocationTypeToWalletLocationTypeConverter())
            .map(WalletLocationType::class.java).to(SmartCardLocationType::class.java, WalletLocationTypeToSmartCardLocationTypeConverter())
            .map(ApiPlace::class.java).to(WalletPlace::class.java, ApiPlaceToWalletPlaceConverter())
            .map(AddressRestResponse::class.java).to(WalletAddress::class.java, getMockAddressConverter())
            .build()

      private fun getMockAddressConverter(): Converter<AddressRestResponse, WalletAddress> {
         val mockConverter = Mockito.mock(HttpAddressToWalletAddressConverter::class.java)
         `when`(mockConverter.convert(Mockito.any(MapperyContext::class.java),
               Mockito.any(AddressRestResponse::class.java))).thenReturn(ImmutableWalletAddress.builder()
               .addressLine("Test address line")
               .countryName("Test country")
               .adminArea("Test admin area")
               .postalCode("Test postal code")
               .build())
         return mockConverter
      }

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

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

      fun mockHttpService(): MockHttpActionService {
         val placesResponse: NearbyResponse = mockPlacesResponse()
         val addressResponse: AddressRestResponse = mock()
         val scLocations: List<SmartCardLocation> = mutableListOf()
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(scLocations)) { request ->
                  Pattern.compile("api/smartcard/provisioning/card_data/[0-9]+/locations").matcher(request.url).find()
               }
               .bind(MockHttpActionService.Response(200)
                     .body(placesResponse)) { request ->
                  request.url.startsWith("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
               }
               .bind(MockHttpActionService.Response(200).body(addressResponse)) {
                  request ->
                  request.url.startsWith("http://maps.googleapis.com/maps/api/geocode/json")
               }
               .bind(MockHttpActionService.Response(204)) { request ->
                  request.url.endsWith("api/user/settings")
               }
               .build()
      }

      fun mockSmartCard(cardId: String): SmartCard {
         val mockedSmartCard: SmartCard = mock()
         whenever(mockedSmartCard.smartCardId()).thenReturn(cardId)
         whenever(mockedSmartCard.cardStatus()).thenReturn(SmartCard.CardStatus.ACTIVE)

         return mockedSmartCard
      }

      fun createMockDb(): SnappyRepository = spy()

      fun createSmartCardLocationInteractor(janet: Janet) = SmartCardLocationInteractor(SessionActionPipeCreator(janet))

      fun createWalletDetectLocationService(): WalletDetectLocationService {
         val location = mockLocation()
         val address = mockAddress()
         val walletDetectLocationService: WalletDetectLocationService = mock()
         whenever(walletDetectLocationService.isPermissionGranted).thenReturn(true)
         whenever(walletDetectLocationService.detectLastKnownLocation()).thenReturn(Observable.just(location))
         return walletDetectLocationService
      }

      fun mockAddress(): Address {
         val address: Address = mock()
         whenever(address.getAddressLine(0)).thenReturn("Test address")
         whenever(address.getAddressLine(1)).thenReturn("Test address 2")
         whenever(address.countryName).thenReturn("USA")
         whenever(address.adminArea).thenReturn("New-York")
         whenever(address.latitude).thenReturn(TEST_LAT)
         whenever(address.longitude).thenReturn(TEST_LNG)
         whenever(address.postalCode).thenReturn("10001")
         return address
      }

      fun mockLocation(): Location {
         val location: Location = mock()
         whenever(location.latitude).thenReturn(TEST_LAT)
         whenever(location.longitude).thenReturn(TEST_LNG)
         whenever(location.provider).thenReturn("TestProvider")
         return location
      }

      fun mockWalletLocation(): WalletLocation {
         val walletLocation: WalletLocation = mock()
         whenever(walletLocation.type()).thenReturn(WalletLocationType.CONNECT)
         whenever(walletLocation.coordinates()).thenReturn(createWalletCoordinates())
         whenever(walletLocation.createdAt()).thenReturn(Calendar.getInstance().time)
         return walletLocation
      }

      fun mockPlacesResponse(): NearbyResponse {
         val apiPlace = mockApiPlace()
         val placesResponse: NearbyResponse = mock()
         whenever(placesResponse.locationPlaces()).thenReturn(mutableListOf(apiPlace))
         return placesResponse
      }

      fun mockApiPlace(): ApiPlace {
         val apiPlace: ApiPlace = mock()
         whenever(apiPlace.name()).thenReturn("Test Place Name")
         return apiPlace
      }

      fun Janet.connectToSmartCardSdk() {
         this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction(ImmutableConnectionParams.of(1)))
               .toBlocking()
               .subscribe()
      }

      fun createWalletCoordinates(): WalletCoordinates {
         return ImmutableWalletCoordinates.builder().lat(TEST_LAT).lng(TEST_LNG).build()
      }

      fun testTrackingStatusCommand(trackingStatusExpected: Boolean, trackingStatusPrevious: Boolean) {
         var trackingStatusActual: Boolean = false
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
         verify(locationStorage, times(1)).isEnableTracking()
         testFetchSubscriber.unsubscribe()
      }

   }
}

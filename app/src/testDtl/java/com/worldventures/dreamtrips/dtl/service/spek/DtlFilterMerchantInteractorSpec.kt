package com.worldventures.dreamtrips.dtl.service.spek

import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper
import org.powermock.core.classloader.annotations.PrepareForTest

@PrepareForTest(DtlLocationHelper::class)
class DtlFilterMerchantInteractorSpec : DtlBaseMerchantSpec({

   beforeEach {
//      initVars()

//      val locationDelegate: LocationDelegate = mock()
//
//      location = mock()
//      whenever(location.latitude).thenReturn(1.0)
//      whenever(location.longitude).thenReturn(1.0)
//      whenever(locationDelegate.lastKnownLocationOrEmpty).thenReturn(location.toSingletonObservable())
//      whenever(locationDelegate.requestLocationUpdate()).thenReturn(location.toSingletonObservable())
//
//      whenever(db.settings).thenReturn(listOf(testDistanceSetting))
//      whenever(db.lastSelectedOffersOnlyToggle).thenReturn(false)
//      whenever(db.amenities).thenReturn(emptyList())
//      filterMerchantInteractor = DtlFilterMerchantInteractor(merchantInteractor, locationInteractor, locationDelegate,
//       SessionActionPipeCreator(janet))
//   }

//   it("should init filter") {
//      checkFilterAction(DtlFilterDataAction.init()) {
//         val result = it.result;
//         result.isDefault
//               && result.distanceType == DistanceType.provideFromSetting(testDistanceSetting)
//      }
//   }

//   it("should reset filter") {
//      checkFilterAction(DtlFilterDataAction.reset()) {
//         it.result != null
//      }
//   }

//   it("should update amenities in the filter") {
//      val amenity = DtlMerchantAttribute("test")
//      checkFilterAction(DtlFilterDataAction.amenitiesUpdate(listOf(amenity))) {
//         val result = it.result
//         result.hasAmenities() && result.amenities.contains(amenity)
//      }
//   }

//   it("should apply filter params") {
//      val maxDistance = 500.0
//      val parameters = ImmutableDtlFilterParameters.builder()
//            .minPrice(2)
//            .maxPrice(3)
//            .maxDistance(maxDistance)
//            .build()
//
//      filterMerchantInteractor.filterDataPipe().send(DtlFilterDataAction.init())
//      checkFilterAction(DtlFilterDataAction.applyParams(parameters)) {
//         val result = it.result
//         !result.isDefault && result.maxDistance == maxDistance
//      }
//   }

//   it("should apply search") {
//      val query = "test"
//      checkFilterAction(DtlFilterDataAction.applySearch(query)) {
//         it.result.searchQuery == query
//      }
//   }

//   it("should apply offers only") {
//      checkFilterAction(DtlFilterDataAction.applyOffersOnly(true)) {
//         it.result.isOffersOnly
//      }
//      verify(db, times(1)).saveLastSelectedOffersOnlyToogle(eq(true))
//   }

//   xit("should update filter after new merchants loading") {
//      val subscriber = TestSubscriber<ActionState<DtlFilterMerchantsAction>>()
//      filterMerchantInteractor.filterMerchantsActionPipe().observe().subscribe(subscriber)
//      locationInteractor.locationPipe().send(
//            LocationCommand.change(
//                  ImmutableDtlManualLocation.builder()
//                        .locationSourceType(LocationSourceType.FROM_MAP)
//                        .analyticsName("test")
//                        .coordinates(com.worldventures.dreamtrips.modules.trips.model.Location(location))
//                        .longName("test")
//                        .build()))
//      checkMerchantActionLoad()
//      subscriber.unsubscribe()
//      assertActionSuccess(subscriber) {
//         !it.result.isEmpty()
//      }
//   }
//
//}) {
//
//   @Before
//   fun mockStatic() { //PowerMock works before running tests only
//      PowerMockito.mockStatic(DtlLocationHelper::class.java)
//      whenever(DtlLocationHelper.selectAcceptableLocation(any(), any())).thenReturn(LatLng(1.0, 1.0))
//      whenever(DtlLocationHelper.calculateDistance(any(), any())).thenReturn(1.0)
//   }
//
////   companion object {
//      //vars to ease use these in a constructor
//      val testDistanceSetting = Setting(DISTANCE_UNITS, Setting.Type.SELECT, KILOMETERS)
//      // late init in beforeEach
//      lateinit var filterMerchantInteractor: DtlFilterMerchantInteractor
//      lateinit var location: Location
//      //

//      fun checkFilterAction(filterDataAction: DtlFilterDataAction, assertPredicate: (DtlFilterDataAction) -> Boolean) {
//         val subscriber = TestSubscriber<ActionState<DtlFilterDataAction>>()
////         filterMerchantInteractor.filterDataPipe()
////               .createObservable(filterDataAction)
////               .subscribe(subscriber)
//         assertActionSuccess(subscriber, Func1 { assertPredicate(filterDataAction) })
   }})

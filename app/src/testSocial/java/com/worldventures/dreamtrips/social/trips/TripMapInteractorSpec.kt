package com.worldventures.dreamtrips.social.trips

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.trip.model.Trip
import com.worldventures.dreamtrips.api.trip.model.TripPinWrapper
import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand
import com.worldventures.dreamtrips.modules.trips.model.Pin
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.trips.service.TripMapInteractor
import com.worldventures.dreamtrips.modules.trips.storage.TripPinsStorage
import com.worldventures.dreamtrips.modules.trips.storage.TripsByUidsStorage
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.MapperyContext
import rx.observers.TestSubscriber
import kotlin.test.assertTrue


class TripMapInteractorSpec : BaseSpec({
   describe("Test getting locations objects") {
      setup(mockHttpServiceForTripLocations())

      context("Storage is empty") {
         whenever(pinStorage.get(any())).thenReturn(emptyList())

         val testSubscriber = TestSubscriber<ActionState<GetTripsLocationsCommand>>()

         tripMapInteractor.mapObjectsPipe()
               .createObservable(GetTripsLocationsCommand("", null))
               .subscribe(testSubscriber)

         it("Should not call onProgress") {
            assertTrue { testSubscriber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } == null }
         }

         it("Items should contain new items") {
            AssertUtil.assertActionSuccess(testSubscriber) { it.items.containsAll(pins) }
         }
      }

      context("Storage is not empty") {
         whenever(pinStorage.get(any())).thenReturn(storedPins)

         val testSubscriber = TestSubscriber<ActionState<GetTripsLocationsCommand>>()

         tripMapInteractor.mapObjectsPipe()
               .createObservable(GetTripsLocationsCommand("", null))
               .subscribe(testSubscriber)

         it("Should call onProgress") {
            assertTrue { testSubscriber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } != null }
         }

         it("Items should contain only new items") {
            AssertUtil.assertActionSuccess(testSubscriber) {
               it.items.containsAll(pins) && !it.items.containsAll(storedPins)
            }
         }
      }
   }

   describe("Test getting trips by uids") {
      setup(mockHttpServiceForTripByUids())

      context("Storage is empty") {
         whenever(tripByUidsStorage.get(any())).thenReturn(emptyList())

         val testSubscriber = TestSubscriber<ActionState<GetTripsByUidCommand>>()

         tripMapInteractor.tripsByUidPipe()
               .createObservable(GetTripsByUidCommand(listOf("1", "2")))
               .subscribe(testSubscriber)

         it("Should not call onProgress") {
            assertTrue { testSubscriber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } == null }
         }

         it("Items should contain new items") {
            AssertUtil.assertActionSuccess(testSubscriber) { it.items.containsAll(trips) }
         }
      }

      context("Storage is not empty and return full set of trips") {
         whenever(tripByUidsStorage.get(any())).thenReturn(storedTrips)

         val testSubscriber = TestSubscriber<ActionState<GetTripsByUidCommand>>()

         tripMapInteractor.tripsByUidPipe()
               .createObservable(GetTripsByUidCommand(listOf("1", "2")))
               .subscribe(testSubscriber)

         it("Should call onProgress") {
            assertTrue { testSubscriber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } != null }
         }

         it("Items should contain new items") {
            AssertUtil.assertActionSuccess(testSubscriber) {
               it.items.containsAll(trips) && it.items.size == trips.size
            }
         }

      }

      context("Storage is not empty and returns not valid data") {
         whenever(tripByUidsStorage.get(any())).thenReturn(storedTripsLessThanNeeded)

         val testSubscriber = TestSubscriber<ActionState<GetTripsByUidCommand>>()

         tripMapInteractor.tripsByUidPipe()
               .createObservable(GetTripsByUidCommand(listOf("1")))
               .subscribe(testSubscriber)

         it("Should not call onProgress") {
            assertTrue { testSubscriber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } == null }
         }

         it("Should return full set of items from api") {
            AssertUtil.assertActionSuccess(testSubscriber) {
               it.items.containsAll(trips) && it.items.size == trips.size
            }
         }

      }
   }

}) {
   companion object {
      val apiPinWrappers: List<TripPinWrapper> = emptyList()
      val apiTrips: List<Trip> = emptyList()

      val pins: List<Pin> = listOf(Pin(), Pin())
      val storedPins: List<Pin> = listOf(Pin(), Pin())

      val trips: List<TripModel> = listOf(generateTripModel("1"), generateTripModel("2"))
      val storedTripsLessThanNeeded: List<TripModel> = listOf(generateTripModel("1"))
      val storedTrips: List<TripModel> = listOf(generateTripModel("1"), generateTripModel("2"))

      val pinStorage: TripPinsStorage = mock()
      val tripByUidsStorage: TripsByUidsStorage = mock()
      val mappery : MapperyContext = mock()

      lateinit var tripMapInteractor: TripMapInteractor

      fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOf(pinStorage, tripByUidsStorage))
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService)
               .build()

         whenever(mappery.convert(apiPinWrappers, Pin::class.java)).thenReturn(pins)
         whenever(mappery.convert(apiTrips, TripModel::class.java)).thenReturn(trips)

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }

         tripMapInteractor = TripMapInteractor(janet)
      }

     fun mockHttpServiceForTripLocations(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(apiPinWrappers)) { it.url.contains("/api/trips/locations") }
               .build()
      }

      fun mockHttpServiceForTripByUids(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(apiTrips)) { it.url.contains("/api/trips/details") }
               .build()
      }

      fun generateTripModel(uid: String): TripModel {
         val trip = TripModel()
         trip.uid = uid
         return trip
      }
   }
}

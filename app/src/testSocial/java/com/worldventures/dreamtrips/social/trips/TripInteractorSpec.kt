package com.worldventures.dreamtrips.social.trips

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.entity.model.EntityHolder
import com.worldventures.dreamtrips.api.trip.model.Trip
import com.worldventures.dreamtrips.api.trip.model.TripWithDetails
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripDetailsCommand
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsCommand
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.trips.model.filter.TripsFilterData
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor
import com.worldventures.dreamtrips.modules.trips.service.storage.TripDetailsStorage
import com.worldventures.dreamtrips.modules.trips.service.storage.TripsDiskStorage
import com.worldventures.dreamtrips.modules.trips.service.storage.TripsStorage
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import kotlin.test.assertTrue

class TripInteractorSpec : BaseSpec({
   describe("Test getting trips action") {
      setup(mockHttpServiceForTrips())

      context("Refresh trips") {
         context("Trips cache is empty") {
            val testSubscriber = TestSubscriber<ActionState<GetTripsCommand>>()

            whenever(tripsMemoryStorage.get(any())).thenReturn(null)
            whenever(tripsDiscStorage.get(any())).thenReturn(emptyList())

            tripsInteractor.tripsPipe
                  .createObservable(GetTripsCommand("", TripsFilterData(), true))
                  .subscribe(testSubscriber)


            it("Should not call onProgress") {
               assertTrue { testSubscriber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } == null }
            }

            it("Items should contain new items") {
               AssertUtil.assertActionSuccess(testSubscriber) { it.items.containsAll(trips) }
            }
         }
         context("Trips cache is not empty") {
            val testSubscriber = TestSubscriber<ActionState<GetTripsCommand>>()

            whenever(tripsMemoryStorage.get(any())).thenReturn(storedTrips)

            tripsInteractor.tripsPipe
                  .createObservable(GetTripsCommand("", TripsFilterData(), true))
                  .subscribe(testSubscriber)

            it("Should call onProgress") {
               assertTrue { testSubscriber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } != null }
            }

            it("Items should contain only new items") {
               AssertUtil.assertActionSuccess(testSubscriber) {
                  it.items.containsAll(trips) && !it.items.containsAll(storedTrips)
               }
            }
         }
      }
      context("Load more trips") {
         context("Trips cache is not empty") {
            val testSubscriber = TestSubscriber<ActionState<GetTripsCommand>>()

            whenever(tripsMemoryStorage.get(any())).thenReturn(storedTrips)

            tripsInteractor.tripsPipe
                  .createObservable(GetTripsCommand("", TripsFilterData(), false))
                  .subscribe(testSubscriber)


            it("Should call onProgress") {
               assertTrue { testSubscriber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } != null }
            }

            it("Items should contain new items and storedItems") {
               AssertUtil.assertActionSuccess(testSubscriber) {
                  it.items.containsAll(trips) &&
                        it.items.containsAll(storedTrips)
               }
            }
         }
      }
   }

   describe("Test getting trip details action") {
      setup(mockHttpServiceForTripDetails())

      context("Cache is not empty, getting trip details") {
         val testSubscriber = TestSubscriber<ActionState<GetTripDetailsCommand>>()

         whenever(snappy.getTripDetail(any())).thenReturn(cachedTrip)

         tripsInteractor.detailsPipe
               .createObservable(GetTripDetailsCommand("1234"))
               .subscribe(testSubscriber)

         it("Should call onProgress") {
            assertTrue { testSubscriber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } != null }
         }

         it("Result should not be empty") {
            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result != null
            }
         }
      }
   }
}) {
   companion object {
      val apiTrips: List<Trip> = emptyList()
      var apiDetailedResponse : EntityHolder<TripWithDetails> = mock()
      var apiTrip: TripWithDetails = mock()

      val trip = generateTripModel("1")
      val cachedTrip = generateTripModel("1")
      val trips: List<TripModel> = listOf(generateTripModel("1"), generateTripModel("2"))
      val storedTrips: List<TripModel> = listOf(generateTripModel("3"), generateTripModel("4"))

      val mappery: MapperyContext = mock()
      val snappy : SnappyRepository = mock()

      val tripsMemoryStorage: PaginatedMemoryStorage<TripModel> = mock()
      val tripsDiscStorage: TripsDiskStorage = mock()
      val storage = TripsStorage(tripsMemoryStorage, tripsDiscStorage)

      val tripDetailsStorage: TripDetailsStorage = TripDetailsStorage(snappy)

      lateinit var tripsInteractor: TripsInteractor

      fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOf(storage, tripDetailsStorage))
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService)
               .build()

         apiDetailedResponse = mock()
         apiTrip = mock()
         whenever(apiDetailedResponse.entity()).thenReturn(apiTrip)

         whenever(mappery.convert(apiTrips, TripModel::class.java))
               .thenReturn(trips)
         whenever(mappery.convert(apiTrip, TripModel::class.java))
               .thenReturn(trip)

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { snappy }

         tripsInteractor = TripsInteractor(SessionActionPipeCreator(janet))
      }

      fun mockHttpServiceForTrips(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(apiTrips)) { it.url.contains("/api/trips") }
               .build()
      }

      fun mockHttpServiceForTripDetails(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(apiDetailedResponse)) { it.url.contains("/api/") }
               .build()
      }

      fun generateTripModel(uid: String): TripModel {
         val trip = TripModel()
         trip.uid = uid
         return trip
      }
   }
}

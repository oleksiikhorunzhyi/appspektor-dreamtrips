package com.worldventures.dreamtrips.dtl.service.spek

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor
import com.worldventures.dreamtrips.modules.dtl.service.action.SearchLocationAction
import io.techery.janet.ActionHolder
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import rx.observers.TestSubscriber

class DtlLocationInteractorSpec : BaseSpec({

   // general mocking for all tests
   // it's necessary for testing chains because there are such tests only
//   val location: DtlExternalLocation = mock()
//   val locationList = listOf(location)
//   whenever(location.longName).thenReturn("London")
//
//   val commandDaggerService = CommandActionService().wrapDagger()
//   val httpStubWrapper = MockHttpActionService.Builder()
//         .bind(MockHttpActionService.Response(200).body(locationList))
//         { request -> request.url.contains("/locations") }
//         .build()
//         .wrapStub()
//
//   val janet = Janet.Builder()
//         .addService(commandDaggerService.wrapCache())
//         .addService(httpStubWrapper.wrapCache())
//         .build()
//
//   commandDaggerService.registerProvider(Janet::class.java, { janet })
//
//   val locationInteractor = DtlLocationInteractor(SessionActionPipeCreator(janet))
//
//   //

//   describe("LocationCommand") {
//      var subscriber = TestSubscriber<ActionState<LocationCommand>>()
//
//      it("should return undefined location at first") {
//         locationInteractor.locationPipe()
//               .observeWithReplay()
//               .subscribe(subscriber)
//         assertActionSuccess(subscriber) { action -> action.result.locationSourceType == LocationSourceType.UNDEFINED }
//      }
//
//      it("changes location to \"${location.longName}\"") {
//         subscriber = TestSubscriber()
//         locationInteractor.locationPipe()
//               .createObservable(LocationCommand.change(location))
//               .subscribe(subscriber)
//         assertActionSuccess(subscriber) { action -> action.result == location }
//      }
//
//      it("checks location after changing") {
//         subscriber = TestSubscriber()
//         locationInteractor.locationPipe().observeWithReplay()
//               .subscribe(subscriber)
//         assertActionSuccess(subscriber) { action -> action.result == location }
//      }
//   }

//   describe("NearbyLocationAction") {
//      val subscriber = TestSubscriber<ActionState<NearbyLocationAction>>()
//
//      it("should call HttpActionService") {
//         val spyHttpCallback = httpStubWrapper.spyCallback()
//         locationInteractor.nearbyLocationPipe()
//               .createObservable(NearbyLocationAction(mock()))
//               .subscribe(subscriber)
//         assertActionSuccess(subscriber) { action -> action.result.isNotEmpty() }
//         verify(spyHttpCallback).onSend(any<ActionHolder<Any>>())
//      }
//   }
//
//   describe("SearchLocationAction") {
//      var subscriber = TestSubscriber<ActionState<SearchLocationAction>>()
//      var spyHttpCallback = httpStubWrapper.spyCallback()
//
//      it("should skip searching when query#length() < 3") {
//         locationInteractor.searchLocationPipe()
//               .createObservable(SearchLocationAction(location.longName.substring(0, 2)))
//               .subscribe(subscriber)
//         assertActionSuccess(subscriber) { action -> action.result.isEmpty() }
//         verify(spyHttpCallback, never()).onSend(any<ActionHolder<Any>>())
//      }
//
//      it("should send http request when query#length() >= 3") {
//         subscriber = TestSubscriber<ActionState<SearchLocationAction>>()
//         spyHttpCallback = httpStubWrapper.spyCallback()
//         locationInteractor.searchLocationPipe()
//               .createObservable(SearchLocationAction(location.longName.substring(0, 3)))
//               .subscribe(subscriber)
//         assertActionSuccess(subscriber) { action -> !action.result.isEmpty() }
//         verify(spyHttpCallback).onSend(any<ActionHolder<Any>>())
//      }
//
//      it("should use cache") {
//         subscriber = TestSubscriber<ActionState<SearchLocationAction>>()
//         spyHttpCallback = httpStubWrapper.spyCallback()
//         locationInteractor.searchLocationPipe()
//               .createObservable(SearchLocationAction(location.longName))
//               .subscribe(subscriber)
//         assertActionSuccess(subscriber) { action -> !action.result.isEmpty() }
//         verify(spyHttpCallback, never()).onSend(any<ActionHolder<Any>>())
//      }
//   }
})

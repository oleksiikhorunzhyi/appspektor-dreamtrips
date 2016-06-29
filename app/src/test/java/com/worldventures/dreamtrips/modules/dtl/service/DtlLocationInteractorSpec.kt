package com.worldventures.dreamtrips.modules.dtl.service

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.core.test.BaseSpec
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction
import io.techery.janet.ActionHolder
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import rx.observers.TestSubscriber

class DtlLocationInteractorSpec : BaseSpec({

    // general mocking for all tests
    // it's necessary for testing chains because there are such tests only
    val location: DtlExternalLocation = mock()
    val locationList = listOf(location)
    whenever(location.longName).thenReturn("London")

    val commandDaggerService = CommandActionService().wrapDagger()
    val httpStubWrapper = MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(200).body(locationList))
            { request -> request.url.contains("/locations") }
            .build()
            .wrapStub()

    val janet = Janet.Builder()
            .addService(commandDaggerService.wrapCache())
            .addService(httpStubWrapper.wrapCache())
            .build()

    commandDaggerService.registerProvider(Janet::class.java, { janet })

    val locationInteractor = DtlLocationInteractor(janet)

    //

    describe("DtlLocationCommand") {
        var subscriber = TestSubscriber<ActionState<DtlLocationCommand>>()

        it("should return undefined location at first") {
            locationInteractor.locationPipe()
                    .createObservable(DtlLocationCommand.last())
                    .subscribe(subscriber)
            assertActionSuccess(subscriber) { action -> action.result.locationSourceType == LocationSourceType.UNDEFINED }
        }

        it("changes location to \"${location.longName}\"") {
            subscriber = TestSubscriber()
            locationInteractor.locationPipe()
                    .createObservable(DtlLocationCommand.change(location))
                    .subscribe(subscriber)
            assertActionSuccess(subscriber) { action -> action.result == location }
        }

        it("checks location after changing") {
            subscriber = TestSubscriber()
            locationInteractor.locationPipe().createObservable(DtlLocationCommand.last())
                    .subscribe(subscriber)
            assertActionSuccess(subscriber) { action -> action.result == location }
        }
    }

    describe("DtlNearbyLocationAction") {
        val subscriber = TestSubscriber<ActionState<DtlNearbyLocationAction>>()

        it("should call HttpActionService") {
            val spyHttpCallback = httpStubWrapper.spyCallback()
            locationInteractor.nearbyLocationPipe()
                    .createObservable(DtlNearbyLocationAction(mock()))
                    .subscribe(subscriber)
            assertActionSuccess(subscriber) { action -> action.result.isNotEmpty() }
            verify(spyHttpCallback).onSend(any<ActionHolder<Any>>())
        }
    }

    describe("DtlSearchLocationAction") {
        var subscriber = TestSubscriber<ActionState<DtlSearchLocationAction>>()
        var spyHttpCallback = httpStubWrapper.spyCallback()

        it("should skip searching when query#length() < 3") {
            locationInteractor.searchLocationPipe()
                    .createObservable(DtlSearchLocationAction(location.longName.substring(0, 2)))
                    .subscribe(subscriber)
            assertActionSuccess(subscriber) { action -> action.result.isEmpty() }
            verify(spyHttpCallback, never()).onSend(any<ActionHolder<Any>>())
        }

        it("should send http request when query#length() >= 3") {
            subscriber = TestSubscriber<ActionState<DtlSearchLocationAction>>()
            spyHttpCallback = httpStubWrapper.spyCallback()
            locationInteractor.searchLocationPipe()
                    .createObservable(DtlSearchLocationAction(location.longName.substring(0, 3)))
                    .subscribe(subscriber)
            assertActionSuccess(subscriber) { action -> !action.result.isEmpty() }
            verify(spyHttpCallback).onSend(any<ActionHolder<Any>>())
        }

        it("should use cache") {
            subscriber = TestSubscriber<ActionState<DtlSearchLocationAction>>()
            spyHttpCallback = httpStubWrapper.spyCallback()
            locationInteractor.searchLocationPipe()
                    .createObservable(DtlSearchLocationAction(location.longName))
                    .subscribe(subscriber)
            assertActionSuccess(subscriber) { action -> !action.result.isEmpty() }
            verify(spyHttpCallback, never()).onSend(any<ActionHolder<Any>>())
        }

    }

})

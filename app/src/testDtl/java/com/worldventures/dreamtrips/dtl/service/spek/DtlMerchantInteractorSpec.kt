package com.worldventures.dreamtrips.dtl.service.spek

import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand
import io.techery.janet.ActionState
import rx.observers.TestSubscriber

class DtlMerchantInteractorSpec : DtlBaseMerchantSpec({

//   val updateAmenitiesSubscriber = TestSubscriber<ActionState<DtlUpdateAmenitiesAction>>()
//   val filterDataSubscriber = TestSubscriber<ActionState<DtlFilterDataAction>>()
   val locationSubscriber = TestSubscriber<ActionState<DtlLocationCommand>>()

//   given("Setup") {
//      on("init basic variables") {
//         initVars()
//      }
//      on("subscribe DtlUpdateAmenitiesAction") {
//         janet.createPipe(DtlUpdateAmenitiesAction::class.java).observe()
//               .subscribe(updateAmenitiesSubscriber)
//      }
//      on("subscribe DtlFilterDataAction") {
//         janet.createPipe(DtlFilterDataAction::class.java).observe()
//               .subscribe(filterDataSubscriber)
//      }
//      on("subscribe DtlLocationCommand") {
//         janet.createPipe(DtlLocationCommand::class.java).observe().subscribe(locationSubscriber)
//      }
//   }
//
//   describe("Sending DtlMerchantsAction") {
//      xit("should send request to load merchants") {
//         checkMerchantActionLoad()
//      }
//      xit("should get merchants from cache") {
//         checkMerchantActionRestore()
//      }
//      xit("checks updating amenities which happened after loading merchants") {
//         updateAmenitiesSubscriber.unsubscribe()
//         assertActionSuccess(updateAmenitiesSubscriber) { action -> action.result != null }
//         verify(db).saveAmenities(anyCollection())
//      }
//      xit("checks changing filter after loading merchants") {
//         filterDataSubscriber.unsubscribe()
//         assertActionSuccess(filterDataSubscriber) { action -> action.result != null }
//      }
//      xit("checks changing location after loading merchants") {
//         locationSubscriber.unsubscribe()
//         assertActionSuccess(locationSubscriber) { action -> action.result != null }
//      }
//      xit("should get merchant by mocked id") {
//         val subscriber = TestSubscriber<ActionState<DtlMerchantByIdAction>>()
//         merchantInteractor.merchantByIdPipe()
//               .createObservable(DtlMerchantByIdAction(MERCHANT_ID))
//               .subscribe(subscriber)
//         assertActionSuccess(subscriber) { action -> action.result != null }
//      }
//   }
})


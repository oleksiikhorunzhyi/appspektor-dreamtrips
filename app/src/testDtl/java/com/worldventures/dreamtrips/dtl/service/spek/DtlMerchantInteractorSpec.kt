package com.worldventures.dreamtrips.dtl.service.spek

import com.nhaarman.mockito_kotlin.anyCollection
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.dreamtrips.AssertUtil.*
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlUpdateAmenitiesAction
import io.techery.janet.ActionState
import rx.observers.TestSubscriber

class DtlMerchantInteractorSpec : DtlBaseMerchantSpec({

   val updateAmenitiesSubscriber = TestSubscriber<ActionState<DtlUpdateAmenitiesAction>>()
   val filterDataSubscriber = TestSubscriber<ActionState<DtlFilterDataAction>>()
   val locationSubscriber = TestSubscriber<ActionState<DtlLocationCommand>>()

   given("Setup") {
      on("init basic variables") {
         initVars()
      }
      on("subscribe DtlUpdateAmenitiesAction") {
         janet.createPipe(DtlUpdateAmenitiesAction::class.java).observe()
               .subscribe(updateAmenitiesSubscriber)
      }
      on("subscribe DtlFilterDataAction") {
         janet.createPipe(DtlFilterDataAction::class.java).observe()
               .subscribe(filterDataSubscriber)
      }
      on("subscribe DtlLocationCommand") {
         janet.createPipe(DtlLocationCommand::class.java).observe().subscribe(locationSubscriber)
      }
   }

   describe("Sending DtlMerchantsAction") {
      it("should send request to load merchants") {
         checkMerchantActionLoad()
      }
      it("should get merchants from cache") {
         checkMerchantActionRestore()
      }
      it("checks updating amenities which happened after loading merchants") {
         updateAmenitiesSubscriber.unsubscribe()
         assertActionSuccess(updateAmenitiesSubscriber) { action -> action.result != null }
         verify(db).saveAmenities(anyCollection())
      }
      it("checks changing filter after loading merchants") {
         filterDataSubscriber.unsubscribe()
         assertActionSuccess(filterDataSubscriber) { action -> action.result != null }
      }
      it("checks changing location after loading merchants") {
         locationSubscriber.unsubscribe()
         assertActionSuccess(locationSubscriber) { action -> action.result != null }
      }
      it("should get merchant by mocked id") {
         val subscriber = TestSubscriber<ActionState<DtlMerchantByIdAction>>()
         merchantInteractor.merchantByIdPipe()
               .createObservable(DtlMerchantByIdAction(MERCHANT_ID))
               .subscribe(subscriber)
         assertActionSuccess(subscriber) { action -> action.result != null }
      }
   }
})


package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CancelCompoundOperationCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.StartNextCompoundOperationCommand
import io.techery.janet.ActionState
import rx.observers.TestSubscriber

class CancelCompoundOperationSpec : BaseUploadingInteractorSpec({
   describe("Cancel compound operation command") {
      initJanet(removeCompoundOperationsNotEmptyContract())

      on("Canceling compound operation") {
         val testSubscribeRemoved = TestSubscriber<ActionState<CompoundOperationsCommand>>()
         val testSubscriberStartNext = TestSubscriber<ActionState<StartNextCompoundOperationCommand>>()
         val testSubscriberCancel = TestSubscriber<ActionState<CancelCompoundOperationCommand>>()

         compoundOperationsInteractor.compoundOperationsPipe()
               .observe()
               .subscribe(testSubscribeRemoved)
         backgroundUploadingInteractor.startNextCompoundPipe().observe().subscribe(testSubscriberStartNext)

         backgroundUploadingInteractor.cancelCompoundOperationPipe()
               .createObservable(CancelCompoundOperationCommand(createPostCompoundOperationModel()))
               .subscribe(testSubscriberCancel)

         it("Should start next compound operation command") {
            AssertUtil.assertStatusCount(testSubscriberCancel, ActionState.Status.SUCCESS, 1)
            AssertUtil.assertStatusCount(testSubscriberStartNext, ActionState.Status.START, 1)
         }

         it("Should call compound operations command") {
            AssertUtil.assertStatusCount(testSubscribeRemoved, ActionState.Status.START, 1)
         }
      }
   }

})
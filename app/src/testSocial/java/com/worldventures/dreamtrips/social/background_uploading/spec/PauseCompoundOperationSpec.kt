package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PauseCompoundOperationCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.StartNextCompoundOperationCommand
import io.techery.janet.ActionState
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class PauseCompoundOperationSpec : BaseUploadingInteractorSpec({
   describe("Pause compound operation command") {
      initJanet(compoundOperationsNotEmptyContract())

      context("Pausing compound operation") {
         val testSubscriberStartNext = TestSubscriber<ActionState<StartNextCompoundOperationCommand>>()
         val testSubscriberPause = TestSubscriber<ActionState<PauseCompoundOperationCommand>>()

         backgroundUploadingInteractor.startNextCompoundPipe().observe().subscribe(testSubscriberStartNext)
         backgroundUploadingInteractor.pauseCompoundOperationPipe()
               .createObservable(PauseCompoundOperationCommand(createPostCompoundOperationModel()))
               .subscribe(testSubscriberPause)

         it("Should start next compound operation and compound operation should be paused") {
            AssertUtil.assertStatusCount(testSubscriberStartNext, ActionState.Status.START, 1)
            AssertUtil.assertActionSuccess(testSubscriberPause) {
               it.result.state() == CompoundOperationState.PAUSED
            }
         }
      }
   }
})
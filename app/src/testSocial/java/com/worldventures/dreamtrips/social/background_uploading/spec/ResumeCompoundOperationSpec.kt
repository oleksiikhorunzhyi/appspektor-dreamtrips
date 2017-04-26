package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.modules.background_uploading.service.command.ResumeCompoundOperationCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.StartNextCompoundOperationCommand
import io.techery.janet.ActionState
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xdescribe
import rx.observers.TestSubscriber

class ResumeCompoundOperationSpec : BaseUploadingInteractorSpec({
   xdescribe("Resume compound operation command") {
      initJanet(compoundOperationsNotEmptyContract())

      context("Resuming compound operation") {
         val testSubscriberStartNext = TestSubscriber<ActionState<StartNextCompoundOperationCommand>>()
         val testSubscriberResume = TestSubscriber<ActionState<ResumeCompoundOperationCommand>>()

         backgroundUploadingInteractor.startNextCompoundPipe().observe().subscribe(testSubscriberStartNext)
         backgroundUploadingInteractor.resumeCompoundOperationPipe()
               .createObservable(ResumeCompoundOperationCommand(createPostCompoundOperationModel()))
               .subscribe(testSubscriberResume)

         it("Should start next compound operation and compound operation should be paused") {
            AssertUtil.assertStatusCount(testSubscriberStartNext, ActionState.Status.START, 1)
            AssertUtil.assertActionSuccess(testSubscriberResume) {
               it.result.state() == CompoundOperationState.SCHEDULED
            }
         }
      }
   }
})
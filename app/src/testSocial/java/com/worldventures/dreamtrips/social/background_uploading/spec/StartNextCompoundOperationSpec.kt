package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PostProcessingCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.StartNextCompoundOperationCommand
import io.techery.janet.ActionState
import rx.observers.TestSubscriber

class StartNextCompoundOperationSpec : BaseUploadingInteractorSpec({
   describe("Start next compound operation") {
      context("Has scheduled and doesn't have started compound operations") {
         initJanet(compoundOperationsHasScheduledAndDontHaveStarted())

         on("Starting next compound operation") {
            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand>>()

            backgroundUploadingInteractor.postProcessingPipe().observe().subscribe(testSubscriber)
            backgroundUploadingInteractor.startNextCompoundPipe().send(StartNextCompoundOperationCommand())

            it("Should start new post processing command") {
               AssertUtil.assertStatusCount(testSubscriber, ActionState.Status.START, 1)
            }
         }
      }

      context("Has scheduled and has started compound operations") {
         initJanet(compoundOperationsHasScheduledAndHasStarted())

         on("Starting next compound operation") {
            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand>>()

            backgroundUploadingInteractor.postProcessingPipe().observe().subscribe(testSubscriber)
            backgroundUploadingInteractor.startNextCompoundPipe().send(StartNextCompoundOperationCommand())

            it("Should not start new post processing command") {
               AssertUtil.assertStatusCount(testSubscriber, ActionState.Status.START, 0)
            }
         }
      }
   }
})
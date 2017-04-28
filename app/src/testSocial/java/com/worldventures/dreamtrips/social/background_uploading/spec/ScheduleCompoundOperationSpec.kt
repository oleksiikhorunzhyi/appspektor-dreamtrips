package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.modules.background_uploading.service.command.ScheduleCompoundOperationCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.StartNextCompoundOperationCommand
import io.techery.janet.ActionState
import rx.observers.TestSubscriber

class ScheduleCompoundOperationSpec : BaseUploadingInteractorSpec({
   describe("Scheduling compound operations") {
      context("Queue is empty") {
         initJanet(compoundOperationsEmptyContract())

         on("Scheduling compound operation") {
            val testSubscriber = TestSubscriber<ActionState<StartNextCompoundOperationCommand>>()

            backgroundUploadingInteractor.startNextCompoundPipe().observe().subscribe(testSubscriber)
            backgroundUploadingInteractor.scheduleOperationPipe().send(ScheduleCompoundOperationCommand(createPostCompoundOperationModel()))

            it("Should start new compound operation command") {
               AssertUtil.assertStatusCount(testSubscriber, ActionState.Status.START, 1)
            }
         }
      }

      context("Queue is not empty") {
         initJanet(compoundOperationsNotEmptyContract())

         on("Scheduling compound operation") {
            val testSubscriber = TestSubscriber<ActionState<StartNextCompoundOperationCommand>>()

            backgroundUploadingInteractor.startNextCompoundPipe().observe().subscribe(testSubscriber)
            backgroundUploadingInteractor.scheduleOperationPipe().send(ScheduleCompoundOperationCommand(createPostCompoundOperationModel()))

            it("Should not start new compound operation") {
               AssertUtil.assertStatusCount(testSubscriber, ActionState.Status.START, 0)
            }
         }
      }
   }
})
package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PostProcessingCommand
import io.techery.janet.ActionState
import rx.observers.TestSubscriber

class PostProcessingSpec : BaseUploadingInteractorSpec({
   describe("Post processing command") {
      context("Result is success") {
         initJanet()

         on("Creating post without attachment") {
            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand>>()
            val testSubscriberCompoundOperations = TestSubscriber<ActionState<CompoundOperationsCommand>>()

            compoundOperationsInteractor.compoundOperationsPipe().observe().subscribe(testSubscriberCompoundOperations)
            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(PostProcessingCommand(createPostCompoundOperationModel(createPostBodyWithoutAttachments())))
                  .subscribe(testSubscriber)

            it("Result should contain created post") {
               AssertUtil.assertActionSuccess(testSubscriber) {
                  it.result.state() == CompoundOperationState.FINISHED
                  it.result.body().uploadedPhotos() == null
                  it.result.body().createdPost() == mockCreatedPost
               }
            }

            it("Compound operations should be updated 4 times") {
               AssertUtil.assertStatusCount(testSubscriberCompoundOperations, ActionState.Status.SUCCESS, 4)
            }
         }
      }


      context("Result is success") {
         initJanet()

         on("Creating post with attachments") {
            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand>>()
            val testSubscriberCompoundOperations = TestSubscriber<ActionState<CompoundOperationsCommand>>()

            compoundOperationsInteractor.compoundOperationsPipe().observe().subscribe(testSubscriberCompoundOperations)
            val postProcessingCommand = PostProcessingCommand(createPostCompoundOperationModel(createPostBodyWithScheduledAttachments()))
            postProcessingCommand.setCompoundOperationDeletionDelay(0)
            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(postProcessingCommand)
                  .subscribe(testSubscriber)

            it("Result should contain list of uploaded photos and created post") {
               AssertUtil.assertActionSuccess(testSubscriber) {
                  it.result.state() == CompoundOperationState.FINISHED
                  it.result.body().uploadedPhotos() == listOfMockPhotos
                  it.result.body().createdPost() == mockCreatedPost
               }
            }

            it("Compound operations should be updated 7 times") {
               AssertUtil.assertStatusCount(testSubscriberCompoundOperations, ActionState.Status.SUCCESS, 7)
            }
         }
      }

      context("Result is not success, photo uploading fails") {
         initJanet(photoUploadingFailsContract())

         on("Creating post with attachments") {
            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand>>()

            val postProcessingCommand = PostProcessingCommand(createPostCompoundOperationModel(createPostBodyWithScheduledAttachments()))
            postProcessingCommand.setCompoundOperationDeletionDelay(0)
            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(postProcessingCommand)
                  .subscribe(testSubscriber)

            it("Command should fail and compound operation should not contain photos and/or created post") {
               AssertUtil.assertActionStateFail(testSubscriber) {
                  it.action.postCompoundOperationModel.body().uploadedPhotos() == null
                  it.action.postCompoundOperationModel.body().createdPost() == null
               }
            }
         }
      }

      context("Result is not success, photo creation fails") {
         initJanet(photoCreatingFailsContract())

         on("Creating post with attachments") {
            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand>>()

            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(PostProcessingCommand(createPostCompoundOperationModel(createPostBodyWithScheduledAttachments())))
                  .subscribe(testSubscriber)

            it("Command should fail and compound operation should not contain photos and/or created post") {
               AssertUtil.assertActionStateFail(testSubscriber) {
                  it.action.postCompoundOperationModel.body().uploadedPhotos() == null
                  it.action.postCompoundOperationModel.body().createdPost() == null
               }
            }
         }
      }

      context("Result is not success, post creation fails") {
         initJanet(postCreatingFailsContract())

         on("Creating post with attachments") {
            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand>>()

            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(PostProcessingCommand(createPostCompoundOperationModel(createPostBodyWithScheduledAttachments())))
                  .subscribe(testSubscriber)

            it("Command should fail, compound operation should contain photos and should not contain created post") {
               AssertUtil.assertActionStateFail(testSubscriber) {
                  it.action.postCompoundOperationModel.body().uploadedPhotos() == listOfMockPhotos
                  it.action.postCompoundOperationModel.body().createdPost() == null
               }
            }
         }
      }
   }
})
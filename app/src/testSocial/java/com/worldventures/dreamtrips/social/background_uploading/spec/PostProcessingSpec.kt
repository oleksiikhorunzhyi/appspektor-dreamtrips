package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.modules.background_uploading.model.*
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PostProcessingCommand
import io.techery.janet.ActionState
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xdescribe
import rx.observers.TestSubscriber

class PostProcessingSpec : BaseUploadingInteractorSpec({
   describe("Post processing command") {
      context("Creating post without attachment") {
         initJanet()

         val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()
         val testSubscriberCompoundOperations = TestSubscriber<ActionState<CompoundOperationsCommand>>()

         compoundOperationsInteractor.compoundOperationsPipe().observe().subscribe(testSubscriberCompoundOperations)
         backgroundUploadingInteractor.postProcessingPipe()
               .createObservable(PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(
                     createPostBodyWithoutAttachments())))
               .subscribe(testSubscriber)

         it("Result should contain created post") {
            AssertUtil.assertActionSuccess(testSubscriber) {
               it.getResult().state() == CompoundOperationState.FINISHED
               it.getResult().body().createdPost() == mockCreatedPost
            }
         }

         it("Compound operations should be updated 4 times") {
            AssertUtil.assertStatusCount(testSubscriberCompoundOperations, ActionState.Status.SUCCESS, 4)
         }
      }

      context("Creating post with photo attachments") {
         initJanet()

         val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()
         val testSubscriberCompoundOperations = TestSubscriber<ActionState<CompoundOperationsCommand>>()

         compoundOperationsInteractor.compoundOperationsPipe().observe().subscribe(testSubscriberCompoundOperations)
         val postProcessingCommand = PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(
               createPostBodyWithScheduledPhotos()))

         postProcessingCommand.setCompoundOperationDeletionDelay(0)
         backgroundUploadingInteractor.postProcessingPipe()
               .createObservable(postProcessingCommand)
               .subscribe(testSubscriber)

         it("Result should contain list of uploaded photos and created post") {
            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.state() == CompoundOperationState.FINISHED
               it.result.body() is PostWithPhotoAttachmentBody
               (it.result.body() as PostWithPhotoAttachmentBody).uploadedPhotos() == listOfMockPhotos
               it.result.body().createdPost() == mockCreatedPost
            }
         }

         it("Compound operations should be updated 7 times") {
            AssertUtil.assertStatusCount(testSubscriberCompoundOperations, ActionState.Status.SUCCESS, 7)
         }
      }

      context("Creating post with video attachment") {
         initJanet()

         val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()
         val testSubscriberCompoundOperations = TestSubscriber<ActionState<CompoundOperationsCommand>>()

         compoundOperationsInteractor.compoundOperationsPipe().observe().subscribe(testSubscriberCompoundOperations)

         val postProcessingCommand = PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(
               createPostBodyWithScheduledVideo()))
         postProcessingCommand.setCompoundOperationDeletionDelay(0)
         backgroundUploadingInteractor.postProcessingPipe()
               .createObservable(postProcessingCommand)
               .subscribe(testSubscriber)

         it("State should be PROCESSING and Result should contains video uid") {
            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.state() == CompoundOperationState.PROCESSING
               it.result.body() is PostWithVideoAttachmentBody
               val uid = (it.result.body() as PostWithVideoAttachmentBody).videoUid()
               uid != null
            }
         }

         it("Compound operations should be updated 6 times") {
            AssertUtil.assertStatusCount(testSubscriberCompoundOperations, ActionState.Status.SUCCESS, 6)
         }
      }

      context("Creating post with photo attachments, photo uploading fails") {
         initJanet(mediaUploadingFailsContract())

         val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()

         val postProcessingCommand = PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(createPostBodyWithScheduledPhotos()))
         postProcessingCommand.setCompoundOperationDeletionDelay(0)
         backgroundUploadingInteractor.postProcessingPipe()
               .createObservable(postProcessingCommand)
               .subscribe(testSubscriber)

         it("Command should fail and compound operation should not contain photos and/or created post") {
            AssertUtil.assertActionStateFail(testSubscriber) {
               it.action.postCompoundOperationModel.body() is PostWithPhotoAttachmentBody
               (it.action.postCompoundOperationModel.body() as PostWithPhotoAttachmentBody).attachments() == null
               it.action.postCompoundOperationModel.body().createdPost() == null
            }
         }
      }

      context("Creating post with video attachment, video uploading fails") {
         initJanet(mediaUploadingFailsContract())

         val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()

         val postProcessingCommand = PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(createPostBodyWithScheduledVideo()))
         postProcessingCommand.setCompoundOperationDeletionDelay(0)
         backgroundUploadingInteractor.postProcessingPipe()
               .createObservable(postProcessingCommand)
               .subscribe(testSubscriber)

         it("Command should fail and compound operation should contain url and no created post") {
            AssertUtil.assertActionStateFail(testSubscriber) {
               val body = it.action.postCompoundOperationModel.body() as PostWithVideoAttachmentBody
               body.state() == PostBody.State.FAILED
               body.videoUid() != null && body.videoUid().isNullOrEmpty()
               it.action.postCompoundOperationModel.body().createdPost() == null
            }
         }
      }

      context("Creating post with photo attachments, photo creation fails") {
         initJanet(photoCreatingFailsContract())

         val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()

         backgroundUploadingInteractor.postProcessingPipe()
               .createObservable(PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(createPostBodyWithScheduledPhotos())))
               .subscribe(testSubscriber)

         it("Command should fail and compound operation should not contain photos and/or created post") {
            AssertUtil.assertActionStateFail(testSubscriber) {
               it.action.postCompoundOperationModel.body() is PostWithPhotoAttachmentBody
               (it.action.postCompoundOperationModel.body() as PostWithPhotoAttachmentBody).uploadedPhotos() == null
               it.action.postCompoundOperationModel.body().createdPost() == null
            }
         }
      }

      context("Creating post with photo attachments, post creation fails") {
         initJanet(postCreatingFailsContract())

         val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()

         backgroundUploadingInteractor.postProcessingPipe()
               .createObservable(PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(createPostBodyWithScheduledPhotos())))
               .subscribe(testSubscriber)

         it("Command should fail, compound operation should contain photos and should not contain created post") {
            AssertUtil.assertActionStateFail(testSubscriber) {
               it.action.postCompoundOperationModel.body() is PostWithPhotoAttachmentBody
               (it.action.postCompoundOperationModel.body() as PostWithPhotoAttachmentBody).uploadedPhotos() == listOfMockPhotos
               it.action.postCompoundOperationModel.body().createdPost() == null
            }
         }
      }
   }
})
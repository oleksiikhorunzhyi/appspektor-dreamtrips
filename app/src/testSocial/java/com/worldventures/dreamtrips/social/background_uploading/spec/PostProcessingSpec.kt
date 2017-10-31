package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostBody
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithPhotoAttachmentBody
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.PostProcessingCommand
import io.techery.janet.ActionState
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class PostProcessingSpec : BaseUploadingInteractorSpec({
   describe("Post processing command") {
      context("Creating post without attachment") {
         it("Result should contain created post and compound operations should be updated 4 times") {
            initJanet()

            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()
            val testSubscriberCompoundOperations = TestSubscriber<ActionState<CompoundOperationsCommand>>()

            compoundOperationsInteractor.compoundOperationsPipe().observe().subscribe(testSubscriberCompoundOperations)
            val command = PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(
                  createPostBodyWithoutAttachments()))
            command.setCompoundOperationDeletionDelay(0)
            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(command)
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber) {
               it.getResult().state() == CompoundOperationState.FINISHED
               it.getResult().body().createdPost() == mockCreatedPost
            }

            AssertUtil.assertStatusCount(testSubscriberCompoundOperations, ActionState.Status.SUCCESS, 4)
         }
      }

      context("Creating post with photo attachments") {
         it("Result should contain list of uploaded photos and created post and compound operations should be updated 7 times") {
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

            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.state() == CompoundOperationState.FINISHED
               it.result.body() is PostWithPhotoAttachmentBody
               (it.result.body() as PostWithPhotoAttachmentBody).uploadedPhotos() == listOfMockPhotos
               it.result.body().createdPost() == mockCreatedPost
            }

            AssertUtil.assertStatusCount(testSubscriberCompoundOperations, ActionState.Status.SUCCESS, 7)
         }
      }

      context("Creating post with video attachment") {
         it("State should be PROCESSING and Result should contains video uid and compound operations should be updated 6 times") {
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

            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.state() == CompoundOperationState.PROCESSING
               it.result.body() is PostWithVideoAttachmentBody
               val uid = (it.result.body() as PostWithVideoAttachmentBody).videoUid()
               uid != null
            }

            AssertUtil.assertStatusCount(testSubscriberCompoundOperations, ActionState.Status.SUCCESS, 6)
         }
      }

      context("Creating post with photo attachments, photo uploading fails") {
         it("Command should fail and compound operation should not contain photos and/or created post") {
            initJanet(mediaUploadingFailsContract())

            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()

            val postProcessingCommand = PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(createPostBodyWithScheduledPhotos()))
            postProcessingCommand.setCompoundOperationDeletionDelay(0)
            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(postProcessingCommand)
                  .subscribe(testSubscriber)

            AssertUtil.assertActionStateFail(testSubscriber) {
               it.action.postCompoundOperationModel.body() is PostWithPhotoAttachmentBody
               (it.action.postCompoundOperationModel.body() as PostWithPhotoAttachmentBody).attachments() == null
               it.action.postCompoundOperationModel.body().createdPost() == null
            }
         }
      }

      context("Creating post with video attachment, video uploading fails") {
         it("Command should fail and compound operation should contain url and no created post") {
            initJanet(mediaUploadingFailsContract())

            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()

            val postProcessingCommand = PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(createPostBodyWithScheduledVideo()))
            postProcessingCommand.setCompoundOperationDeletionDelay(0)
            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(postProcessingCommand)
                  .subscribe(testSubscriber)

            AssertUtil.assertActionStateFail(testSubscriber) {
               val body = it.action.postCompoundOperationModel.body() as PostWithVideoAttachmentBody
               body.state() == PostBody.State.FAILED
               body.videoUid() != null && body.videoUid().isNullOrEmpty()
               it.action.postCompoundOperationModel.body().createdPost() == null
            }
         }
      }

      context("Creating post with photo attachments, photo creation fails") {
         it("Command should fail and compound operation should not contain photos and/or created post") {
            initJanet(photoCreatingFailsContract())

            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()

            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(createPostBodyWithScheduledPhotos())))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionStateFail(testSubscriber) {
               it.action.postCompoundOperationModel.body() is PostWithPhotoAttachmentBody
               (it.action.postCompoundOperationModel.body() as PostWithPhotoAttachmentBody).uploadedPhotos() == null
               it.action.postCompoundOperationModel.body().createdPost() == null
            }
         }
      }

      context("Creating post with photo attachments, post creation fails") {
         it("Command should fail, compound operation should contain photos and should not contain created post") {
            initJanet(postCreatingFailsContract())

            val testSubscriber = TestSubscriber<ActionState<PostProcessingCommand<PostBody>>>()

            backgroundUploadingInteractor.postProcessingPipe()
                  .createObservable(PostProcessingCommand.createPostProcessing(createPostCompoundOperationModel(createPostBodyWithScheduledPhotos())))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionStateFail(testSubscriber) {
               it.action.postCompoundOperationModel.body() is PostWithPhotoAttachmentBody
               (it.action.postCompoundOperationModel.body() as PostWithPhotoAttachmentBody).uploadedPhotos() == listOfMockPhotos
               it.action.postCompoundOperationModel.body().createdPost() == null
            }
         }
      }
   }
})
package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.dreamtrips.modules.background_uploading.model.*
import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoUploadUrls
import com.worldventures.dreamtrips.modules.background_uploading.service.command.DeleteCompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PhotoAttachmentUploadingCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.QueryCompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.UpdateCompoundOperationCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.UploadVideoFileCommand
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle
import com.worldventures.dreamtrips.modules.feed.model.ImmutableSelectedPhoto
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePhotosCommand
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand
import com.worldventures.dreamtrips.modules.trips.model.Location
import com.worldventures.dreamtrips.social.background_uploading.spec.BaseUploadingInteractorSpec.Companion.listOfMockPhotos
import com.worldventures.dreamtrips.social.background_uploading.spec.BaseUploadingInteractorSpec.Companion.mockCreatedPost
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.Contract
import java.util.*

internal fun createPostCompoundOperationModel(body: PostBody = createPostBodyWithoutAttachments(),
                                              state: CompoundOperationState = CompoundOperationState.SCHEDULED) =
      ImmutablePostCompoundOperationModel.builder<PostBody>()
            .id(320)
            .progress(0)
            .creationDate(Date())
            .millisLeft(0)
            .averageUploadSpeed(0.0)
            .state(state)
            .body(body)
            .type(obtainBodyType(body))
            .build()


fun obtainBodyType(body: PostBody): PostBody.Type {
   when (body) {
      is PostWithPhotoAttachmentBody -> return PostBody.Type.PHOTO
      is PostWithVideoAttachmentBody -> return PostBody.Type.VIDEO
      is TextPostBody -> return PostBody.Type.TEXT
   }
   throw RuntimeException("Incorrect body was passed")
}

internal fun createStartedPostCompoundOperationModel() = createPostCompoundOperationModel(state = CompoundOperationState.STARTED)

internal fun createPostBodyWithoutAttachments() =
      ImmutableTextPostBody.builder()
            .text("testText")
            .origin(CreateEntityBundle.Origin.FEED)
            .location(Location(20.0, 20.0))
            .build()

internal fun createPostBodyWithUploadedPhotos() =
      ImmutablePostWithPhotoAttachmentBody.builder()
            .text("testText")
            .location(Location(20.0, 20.0))
            .origin(CreateEntityBundle.Origin.FEED)
            .attachments(arrayListOf(
                  createPhotoAttachment(11, PostBody.State.UPLOADED),
                  createPhotoAttachment(22, PostBody.State.UPLOADED)))
            .build()

internal fun createPostBodyWithScheduledPhotos() =
      ImmutablePostWithPhotoAttachmentBody.builder()
            .text("testText")
            .location(Location(20.0, 20.0))
            .origin(CreateEntityBundle.Origin.FEED)
            .attachments(arrayListOf(
                  createPhotoAttachment(11, PostBody.State.SCHEDULED),
                  createPhotoAttachment(22, PostBody.State.SCHEDULED)))
            .build()

internal fun createPostBodyWithUploadedVideo() =
      ImmutablePostWithVideoAttachmentBody.builder()
            .text("testText")
            .location(Location(20.0, 20.0))
            .origin(CreateEntityBundle.Origin.FEED)
            .remoteURL("www://blabla")
            .videoPath("file://blabla")
            .state(PostBody.State.UPLOADED)
            .aspectRatio(1.7)
            .videoUploadStatus(null)
            .build()

internal fun createVideoUploadUrls() = VideoUploadUrls()

internal fun createPostBodyWithScheduledVideo() =
      ImmutablePostWithVideoAttachmentBody.builder()
            .text("testText")
            .location(Location(20.0, 20.0))
            .origin(CreateEntityBundle.Origin.FEED)
            .remoteURL("www://blabla")
            .videoPath("file://blabla")
            .aspectRatio(1.7)
            .state(PostBody.State.SCHEDULED)
            .videoUploadStatus(null)
            .build()

internal fun createPhotoAttachment(id: Int, state: PostBody.State) =
      ImmutablePhotoAttachment.builder()
            .id(id)
            .progress(0)
            .state(state)
            .selectedPhoto(createSelectedPhoto())
            .build()

internal fun createSelectedPhoto() =
      ImmutableSelectedPhoto.builder()
            .path("testPath")
            .width(100)
            .source(MediaAttachment.Source.GALLERY)
            .height(100)
            .size(1000000)
            .build()

internal fun compoundOperationsEmptyContract() = listOf<Contract>(
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(emptyList<PostCompoundOperationModel<in PostBody>>()))

internal fun compoundOperationsNotEmptyContract() = listOf<Contract>(
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(listOf<PostCompoundOperationModel<in PostBody>>(
            createStartedPostCompoundOperationModel())))

internal fun compoundOperationsHasScheduledAndDontHaveStarted() = listOf<Contract>(
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(listOf<PostCompoundOperationModel<in PostBody>>(
            createPostCompoundOperationModel())),
      BaseContract.of(QueryCompoundOperationsCommand::class.java).result(listOf<PostCompoundOperationModel<in PostBody>>(
            createPostCompoundOperationModel())))

internal fun compoundOperationsHasScheduledAndHasStarted() = listOf<Contract>(
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(listOf<PostCompoundOperationModel<in PostBody>>(
            createPostCompoundOperationModel(), createStartedPostCompoundOperationModel())))

internal fun queryCompoundOperationsNotEmptyContract() = listOf<Contract>(
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(listOf<PostCompoundOperationModel<in PostBody>>(
            createStartedPostCompoundOperationModel(), createPostCompoundOperationModel())))

internal fun removeCompoundOperationsNotEmptyContract() = listOf<Contract>(
      BaseContract.of(DeleteCompoundOperationsCommand::class.java).result(emptyList<PostCompoundOperationModel<in PostBody>>()))

internal fun mediaUploadingFailsContract() = listOf<Contract>(
      BaseContract.of(PhotoAttachmentUploadingCommand::class.java).exception(IllegalStateException()),
      BaseContract.of(UploadVideoFileCommand::class.java).exception(IllegalStateException()),
      BaseContract.of(CreatePhotosCommand::class.java).result(listOfMockPhotos),
      BaseContract.of(CreatePostCommand::class.java).result(mockCreatedPost))

internal fun photoCreatingFailsContract() = listOf<Contract>(
      BaseContract.of(PhotoAttachmentUploadingCommand::class.java)
            .result(createPostCompoundOperationModel(createPostBodyWithUploadedPhotos())),
      BaseContract.of(CreatePhotosCommand::class.java).exception(IllegalStateException()),
      BaseContract.of(CreatePostCommand::class.java).result(mockCreatedPost))

internal fun postCreatingFailsContract() = listOf<Contract>(
      BaseContract.of(PhotoAttachmentUploadingCommand::class.java)
            .result(createPostCompoundOperationModel(createPostBodyWithUploadedPhotos())),
      BaseContract.of(UploadVideoFileCommand::class.java)
            .result(createPostBodyWithUploadedVideo()),
      BaseContract.of(CreatePhotosCommand::class.java).result(listOfMockPhotos),
      BaseContract.of(CreatePostCommand::class.java).exception(IllegalStateException()))

internal fun successContract() = listOf<Contract>(
      BaseContract.of(PhotoAttachmentUploadingCommand::class.java)
            .result(createPostCompoundOperationModel(createPostBodyWithUploadedPhotos())),
      BaseContract.of(UploadVideoFileCommand::class.java)
            .result(createPostBodyWithUploadedVideo()),
      BaseContract.of(CreatePhotosCommand::class.java).result(listOfMockPhotos),
      BaseContract.of(CreatePostCommand::class.java).result(mockCreatedPost))

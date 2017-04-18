package com.worldventures.dreamtrips.social.background_uploading.spec

import com.worldventures.dreamtrips.modules.background_uploading.model.*
import com.worldventures.dreamtrips.modules.background_uploading.service.command.DeleteCompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PhotoAttachmentUploadingCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.QueryCompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.UpdateCompoundOperationCommand
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

internal fun createPostCompoundOperationModel(body: PostWithAttachmentBody = createPostBodyWithoutAttachments(),
                                              state: CompoundOperationState = CompoundOperationState.SCHEDULED) =
      ImmutablePostCompoundOperationModel.builder()
            .id(320)
            .progress(0)
            .creationDate(Date())
            .millisLeft(0)
            .averageUploadSpeed(0.0)
            .state(state)
            .body(body)
            .build()

internal fun createStartedPostCompoundOperationModel() = createPostCompoundOperationModel(state = CompoundOperationState.STARTED)

internal fun createPostBodyWithoutAttachments() =
      ImmutablePostWithAttachmentBody.builder()
            .text("testText")
            .origin(CreateEntityBundle.Origin.FEED)
            .location(Location(20.0, 20.0))
            .attachments(arrayListOf())
            .build()

internal fun createPostBodyWithUploadedAttachments() =
      ImmutablePostWithAttachmentBody.builder()
            .text("testText")
            .location(Location(20.0, 20.0))
            .origin(CreateEntityBundle.Origin.FEED)
            .attachments(arrayListOf(
                  createPhotoAttachment(11, PhotoAttachment.State.UPLOADED),
                  createPhotoAttachment(22, PhotoAttachment.State.UPLOADED)))
            .build()

internal fun createPostBodyWithScheduledAttachments() =
      ImmutablePostWithAttachmentBody.builder()
            .text("testText")
            .location(Location(20.0, 20.0))
            .origin(CreateEntityBundle.Origin.FEED)
            .attachments(arrayListOf(
                  createPhotoAttachment(11, PhotoAttachment.State.SCHEDULED),
                  createPhotoAttachment(22, PhotoAttachment.State.SCHEDULED)))
            .build()

internal fun createPhotoAttachment(id: Int, state: PhotoAttachment.State) =
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
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(emptyList<PostCompoundOperationModel>()))

internal fun compoundOperationsNotEmptyContract() = listOf<Contract>(
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(listOf<PostCompoundOperationModel>(
            createStartedPostCompoundOperationModel())))

internal fun compoundOperationsHasScheduledAndDontHaveStarted() = listOf<Contract>(
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(listOf<PostCompoundOperationModel>(
            createPostCompoundOperationModel())),
      BaseContract.of(QueryCompoundOperationsCommand::class.java).result(listOf<PostCompoundOperationModel>(
            createPostCompoundOperationModel())))

internal fun compoundOperationsHasScheduledAndHasStarted() = listOf<Contract>(
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(listOf<PostCompoundOperationModel>(
            createPostCompoundOperationModel(), createStartedPostCompoundOperationModel())))

internal fun queryCompoundOperationsNotEmptyContract() = listOf<Contract>(
      BaseContract.of(UpdateCompoundOperationCommand::class.java).result(listOf<PostCompoundOperationModel>(
            createStartedPostCompoundOperationModel(), createPostCompoundOperationModel())))

internal fun removeCompoundOperationsNotEmptyContract() = listOf<Contract>(
      BaseContract.of(DeleteCompoundOperationsCommand::class.java).result(emptyList<PostCompoundOperationModel>()))

internal fun photoUploadingFailsContract() = listOf<Contract>(
      BaseContract.of(PhotoAttachmentUploadingCommand::class.java).exception(IllegalStateException()),
      BaseContract.of(CreatePhotosCommand::class.java).result(listOfMockPhotos),
      BaseContract.of(CreatePostCommand::class.java).result(mockCreatedPost))

internal fun photoCreatingFailsContract() = listOf<Contract>(
      BaseContract.of(PhotoAttachmentUploadingCommand::class.java)
            .result(createPostCompoundOperationModel(createPostBodyWithUploadedAttachments())),
      BaseContract.of(CreatePhotosCommand::class.java).exception(IllegalStateException()),
      BaseContract.of(CreatePostCommand::class.java).result(mockCreatedPost))

internal fun postCreatingFailsContract() = listOf<Contract>(
      BaseContract.of(PhotoAttachmentUploadingCommand::class.java)
            .result(createPostCompoundOperationModel(createPostBodyWithUploadedAttachments())),
      BaseContract.of(CreatePhotosCommand::class.java).result(listOfMockPhotos),
      BaseContract.of(CreatePostCommand::class.java).exception(IllegalStateException()))

internal fun successContract() = listOf<Contract>(
      BaseContract.of(PhotoAttachmentUploadingCommand::class.java)
            .result(createPostCompoundOperationModel(createPostBodyWithUploadedAttachments())),
      BaseContract.of(CreatePhotosCommand::class.java).result(listOfMockPhotos),
      BaseContract.of(CreatePostCommand::class.java).result(mockCreatedPost))

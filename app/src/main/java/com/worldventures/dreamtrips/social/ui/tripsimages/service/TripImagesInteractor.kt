package com.worldventures.dreamtrips.social.ui.tripsimages.service

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.CreateReviewPhotoCreationItemCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.AddPhotoTagsCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CheckVideoProcessingStatusCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CreatePhotoCreationItemCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoTagsCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DownloadImageCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.EditPhotoCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.EditPhotoWithTagsCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.FetchLocationFromExifCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesAddedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesRemovedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.UserImagesRemovedCommand

import io.techery.janet.ActionPipe
import rx.schedulers.Schedulers

class TripImagesInteractor(pipeCreator: SessionActionPipeCreator) {

   val baseTripImagesPipe: ActionPipe<BaseMediaCommand> = pipeCreator
         .createPipe(BaseMediaCommand::class.java, Schedulers.io())
   val deletePhotoPipe: ActionPipe<DeletePhotoCommand> = pipeCreator
         .createPipe(DeletePhotoCommand::class.java, Schedulers.io())
   val inspireMePhotosPipe: ActionPipe<GetInspireMePhotosCommand> = pipeCreator
         .createPipe(GetInspireMePhotosCommand::class.java, Schedulers.io())
   val ysbhPhotosPipe: ActionPipe<GetYSBHPhotosCommand> = pipeCreator
         .createPipe(GetYSBHPhotosCommand::class.java, Schedulers.io())
   val deletePhotoTagsPipe: ActionPipe<DeletePhotoTagsCommand> = pipeCreator
         .createPipe(DeletePhotoTagsCommand::class.java, Schedulers.io())
   val createPhotoCreationItemPipe: ActionPipe<CreatePhotoCreationItemCommand> = pipeCreator
         .createPipe(CreatePhotoCreationItemCommand::class.java, Schedulers.io())
   val fetchLocationFromExifPipe: ActionPipe<FetchLocationFromExifCommand> = pipeCreator
         .createPipe(FetchLocationFromExifCommand::class.java, Schedulers.io())
   val editPhotoActionPipe: ActionPipe<EditPhotoCommand> = pipeCreator
         .createPipe(EditPhotoCommand::class.java, Schedulers.io())
   val editPhotoWithTagsCommandActionPipe: ActionPipe<EditPhotoWithTagsCommand> = pipeCreator
         .createPipe(EditPhotoWithTagsCommand::class.java, Schedulers.io())
   val addPhotoTagsActionPipe: ActionPipe<AddPhotoTagsCommand> = pipeCreator.
         createPipe(AddPhotoTagsCommand::class.java, Schedulers.io())
   val downloadImageActionPipe: ActionPipe<DownloadImageCommand> = pipeCreator
         .createPipe(DownloadImageCommand::class.java, Schedulers.io())
   val createReviewPhotoCreationItemPipe: ActionPipe<CreateReviewPhotoCreationItemCommand> = pipeCreator
         .createPipe(CreateReviewPhotoCreationItemCommand::class.java, Schedulers.io())
   val memberImagesAddedCommandPipe: ActionPipe<MemberImagesAddedCommand> = pipeCreator
         .createPipe(MemberImagesAddedCommand::class.java, Schedulers.io())
   val memberImagesRemovedPipe: ActionPipe<MemberImagesRemovedCommand> = pipeCreator
         .createPipe(MemberImagesRemovedCommand::class.java, Schedulers.io())
   val userImagesRemovedPipe: ActionPipe<UserImagesRemovedCommand> = pipeCreator
         .createPipe(UserImagesRemovedCommand::class.java, Schedulers.io())
   val checkVideoProcessingStatusPipe: ActionPipe<CheckVideoProcessingStatusCommand> = pipeCreator
         .createPipe(CheckVideoProcessingStatusCommand::class.java, Schedulers.io())
}

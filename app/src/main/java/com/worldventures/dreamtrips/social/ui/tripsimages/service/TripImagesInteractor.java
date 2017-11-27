package com.worldventures.dreamtrips.social.ui.tripsimages.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.CreateReviewPhotoCreationItemCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CheckVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CreatePhotoCreationItemCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DownloadImageCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.EditPhotoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.EditPhotoWithTagsCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.FetchLocationFromExifCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesAddedCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesRemovedCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.UserImagesRemovedCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class TripImagesInteractor {

   private final ActionPipe<BaseMediaCommand> baseTripImagesActionPipe;
   private final ActionPipe<DeletePhotoCommand> deletePhotoPipe;
   private final ActionPipe<GetInspireMePhotosCommand> inspireMePhotosActionPipe;
   private final ActionPipe<GetYSBHPhotosCommand> ysbhPhotosActionPipe;
   private final ActionPipe<DeletePhotoTagsCommand> deletePhotoTagsActionPipe;
   private final ActionPipe<CreatePhotoCreationItemCommand> createPhotoCreationItemPipe;
   private final ActionPipe<FetchLocationFromExifCommand> fetchLocationFromExifPipe;
   private final ActionPipe<EditPhotoCommand> editPhotoActionPipe;
   private final ActionPipe<EditPhotoWithTagsCommand> editPhotoWithTagsCommandActionPipe;
   private final ActionPipe<AddPhotoTagsCommand> addPhotoTagsActionPipe;
   private final ActionPipe<DownloadImageCommand> downloadImageActionPipe;
   private final ActionPipe<CreateReviewPhotoCreationItemCommand> createReviewPhotoCreationItemPipe;
   private final ActionPipe<MemberImagesAddedCommand> memberImagesAddedCommandPipe;
   private final ActionPipe<MemberImagesRemovedCommand> memberImagesRemovedCommandPipe;
   private final ActionPipe<UserImagesRemovedCommand> userImagesRemovedCommandPipe;
   private final ActionPipe<CheckVideoProcessingStatusCommand> checkVideoProcessingStatusPipe;

   public TripImagesInteractor(SessionActionPipeCreator pipeCreator) {
      this.baseTripImagesActionPipe = pipeCreator.createPipe(BaseMediaCommand.class, Schedulers.io());
      this.deletePhotoPipe = pipeCreator.createPipe(DeletePhotoCommand.class, Schedulers.io());
      this.inspireMePhotosActionPipe = pipeCreator.createPipe(GetInspireMePhotosCommand.class, Schedulers.io());
      this.ysbhPhotosActionPipe = pipeCreator.createPipe(GetYSBHPhotosCommand.class, Schedulers.io());
      this.deletePhotoTagsActionPipe = pipeCreator.createPipe(DeletePhotoTagsCommand.class, Schedulers.io());
      this.createPhotoCreationItemPipe = pipeCreator.createPipe(CreatePhotoCreationItemCommand.class, Schedulers.io());
      this.fetchLocationFromExifPipe = pipeCreator.createPipe(FetchLocationFromExifCommand.class, Schedulers.io());
      this.editPhotoActionPipe = pipeCreator.createPipe(EditPhotoCommand.class, Schedulers.io());
      this.editPhotoWithTagsCommandActionPipe = pipeCreator.createPipe(EditPhotoWithTagsCommand.class, Schedulers.io());
      this.addPhotoTagsActionPipe = pipeCreator.createPipe(AddPhotoTagsCommand.class, Schedulers.io());
      this.downloadImageActionPipe = pipeCreator.createPipe(DownloadImageCommand.class, Schedulers.io());
      this.createReviewPhotoCreationItemPipe = pipeCreator.createPipe(CreateReviewPhotoCreationItemCommand.class, Schedulers
            .io());
      this.memberImagesAddedCommandPipe = pipeCreator.createPipe(MemberImagesAddedCommand.class, Schedulers.io());
      this.memberImagesRemovedCommandPipe = pipeCreator.createPipe(MemberImagesRemovedCommand.class, Schedulers.io());
      this.userImagesRemovedCommandPipe = pipeCreator.createPipe(UserImagesRemovedCommand.class, Schedulers.io());
      this.checkVideoProcessingStatusPipe = pipeCreator.createPipe(CheckVideoProcessingStatusCommand.class, Schedulers.io());
   }

   public ActionPipe<DeletePhotoCommand> deletePhotoPipe() {
      return deletePhotoPipe;
   }

   public ActionPipe<BaseMediaCommand> baseTripImagesCommandActionPipe() {
      return baseTripImagesActionPipe;
   }

   public ActionPipe<GetInspireMePhotosCommand> inspireMePhotosPipe() {
      return inspireMePhotosActionPipe;
   }

   public ActionPipe<GetYSBHPhotosCommand> ysbhPhotosPipe() {
      return ysbhPhotosActionPipe;
   }

   public ActionPipe<DeletePhotoTagsCommand> deletePhotoTagsPipe() {
      return deletePhotoTagsActionPipe;
   }

   public ActionPipe<CreatePhotoCreationItemCommand> createPhotoCreationItemPipe() {
      return createPhotoCreationItemPipe;
   }

   public ActionPipe<FetchLocationFromExifCommand> fetchLocationFromExifPipe() {
      return fetchLocationFromExifPipe;
   }

   public ActionPipe<EditPhotoCommand> editPhotoActionPipe() {
      return editPhotoActionPipe;
   }

   public ActionPipe<EditPhotoWithTagsCommand> editPhotoWithTagsCommandActionPipe() {
      return editPhotoWithTagsCommandActionPipe;
   }

   public ActionPipe<AddPhotoTagsCommand> addPhotoTagsActionPipe() {
      return addPhotoTagsActionPipe;
   }

   public ActionPipe<DownloadImageCommand> downloadImageActionPipe() {
      return downloadImageActionPipe;
   }

   public ActionPipe<CreateReviewPhotoCreationItemCommand> createReviewPhotoCreationItemPipe() {
      return createReviewPhotoCreationItemPipe;
   }

   public ActionPipe<MemberImagesAddedCommand> memberImagesAddedCommandPipe() {
      return memberImagesAddedCommandPipe;
   }

   public ActionPipe<MemberImagesRemovedCommand> memberImagesRemovedPipe() {
      return memberImagesRemovedCommandPipe;
   }

   public ActionPipe<UserImagesRemovedCommand> userImagesRemovedPipe() {
      return userImagesRemovedCommandPipe;
   }

   public ActionPipe<CheckVideoProcessingStatusCommand> checkVideoProcessingStatusPipe() {
      return checkVideoProcessingStatusPipe;
   }
}

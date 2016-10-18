package com.worldventures.dreamtrips.modules.tripsimages.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetInspireMePhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetMembersPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetUserPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetYSBHPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.CreatePhotoCreationItemCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.FetchLocationFromExifCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class TripImagesInteractor {

   private final ActionPipe<CreatePhotoCreationItemCommand> createPhotoCreationItemPipe;
   private final ActionPipe<FetchLocationFromExifCommand> fetchLocationFromExifPipe;
   private final ActionPipe<GetInspireMePhotosCommand> inspireMePhotosActionPipe;
   private final ActionPipe<GetMembersPhotosCommand> membersPhotosActionPipe;
   private final ActionPipe<GetUserPhotosCommand> userPhotosActionPipe;
   private final ActionPipe<GetYSBHPhotosCommand> ysbhPhotosActionPipe;

   public TripImagesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      createPhotoCreationItemPipe = sessionActionPipeCreator.createPipe(CreatePhotoCreationItemCommand.class, Schedulers.io());
      fetchLocationFromExifPipe = sessionActionPipeCreator.createPipe(FetchLocationFromExifCommand.class, Schedulers.io());
      inspireMePhotosActionPipe = sessionActionPipeCreator.createPipe(GetInspireMePhotosCommand.class, Schedulers.io());
      membersPhotosActionPipe = sessionActionPipeCreator.createPipe(GetMembersPhotosCommand.class, Schedulers.io());
      userPhotosActionPipe = sessionActionPipeCreator.createPipe(GetUserPhotosCommand.class, Schedulers.io());
      ysbhPhotosActionPipe = sessionActionPipeCreator.createPipe(GetYSBHPhotosCommand.class, Schedulers.io());
   }

   public ActionPipe<CreatePhotoCreationItemCommand> createPhotoCreationItemPipe() {
      return createPhotoCreationItemPipe;
   }

   public ActionPipe<FetchLocationFromExifCommand> fetchLocationFromExifPipe() {
      return fetchLocationFromExifPipe;
   }

   public ActionPipe<GetInspireMePhotosCommand> getInspireMePhotosPipe() {
      return inspireMePhotosActionPipe;
   }

   public ActionPipe<GetMembersPhotosCommand> getMembersPhotosPipe() {
      return membersPhotosActionPipe;
   }

   public ActionPipe<GetUserPhotosCommand> getUserPhotosPipe() {
      return userPhotosActionPipe;
   }

   public ActionPipe<GetYSBHPhotosCommand> getYSBHPhotosPipe() {
      return ysbhPhotosActionPipe;
   }
}

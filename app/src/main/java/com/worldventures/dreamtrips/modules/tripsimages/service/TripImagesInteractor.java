package com.worldventures.dreamtrips.modules.tripsimages.service;


import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GrabLocationFromExifCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.CreatePhotoCreationItemCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class TripImagesInteractor {

   private final ActionPipe<CreatePhotoCreationItemCommand> createPhotoCreationItemPipe;
   private final ActionPipe<GrabLocationFromExifCommand> grabLocationFromExifPipe;

   public TripImagesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      createPhotoCreationItemPipe = sessionActionPipeCreator.createPipe(CreatePhotoCreationItemCommand.class, Schedulers.io());
      grabLocationFromExifPipe = sessionActionPipeCreator.createPipe(GrabLocationFromExifCommand.class, Schedulers.io());
   }

   public ActionPipe<CreatePhotoCreationItemCommand> createPhotoCreationItemPipe() {
      return createPhotoCreationItemPipe;
   }

   public ActionPipe<GrabLocationFromExifCommand> grabLocationFromExifPipe() {
      return grabLocationFromExifPipe;
   }
}

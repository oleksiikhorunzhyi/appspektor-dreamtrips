package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.GetPhotosHttpAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TripImagesCommand;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetMembersPhotosCommand extends TripImagesCommand<Photo> {

   public GetMembersPhotosCommand(int page, int perPage) {
      super(page, perPage);
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(GetPhotosHttpAction.class)
            .createObservableResult(new GetPhotosHttpAction(page, perPage))
            .map(GetPhotosHttpAction::response)
            .map(photos -> mappery.convert(photos, Photo.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_member_images;
   }
}

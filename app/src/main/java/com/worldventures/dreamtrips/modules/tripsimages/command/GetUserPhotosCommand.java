package com.worldventures.dreamtrips.modules.tripsimages.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.GetPhotosHttpAction;
import com.worldventures.dreamtrips.api.photos.GetPhotosOfUserHttpAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetUserPhotosCommand extends TripImagesCommand<Photo> {
   private int userId;

   public GetUserPhotosCommand(int userId, int page, int perPage) {
      super(page, perPage);
      this.userId = userId;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(GetPhotosOfUserHttpAction.class)
            .createObservableResult(new GetPhotosOfUserHttpAction(userId, page, perPage))
            .map(GetPhotosOfUserHttpAction::response)
            .map(photos -> mappery.convert(photos, Photo.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_member_images;
   }
}

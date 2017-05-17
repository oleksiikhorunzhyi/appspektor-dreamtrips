package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.GetPhotosHttpAction;
import com.worldventures.dreamtrips.api.photos.ImmutablePhotosPaginatedParams;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetMembersPhotosCommand extends TimeBasedPaginatedTripImagesCommand<Photo> {

   public GetMembersPhotosCommand(PaginationParams paginationParams) {
      super(paginationParams);
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(GetPhotosHttpAction.class)
            .createObservableResult(new GetPhotosHttpAction(ImmutablePhotosPaginatedParams.builder()
               .before(before)
               .after(after)
               .pageSize(perPage).build()))
            .map(GetPhotosHttpAction::response)
            .map(photos -> mappery.convert(photos, Photo.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_member_images;
   }
}

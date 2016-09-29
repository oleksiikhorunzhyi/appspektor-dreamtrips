package com.worldventures.dreamtrips.modules.tripsimages.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.GetPhotosOfUserHttpAction;
import com.worldventures.dreamtrips.api.ysbh.GetYSBHPhotosHttpAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetYSBHPhotosCommand extends TripImagesCommand<YSBHPhoto> {

   public GetYSBHPhotosCommand(int page, int perPage) {
      super(page, perPage);
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(GetYSBHPhotosHttpAction.class)
            .createObservableResult(new GetYSBHPhotosHttpAction(page, perPage))
            .map(GetYSBHPhotosHttpAction::response)
            .map(photos -> mappery.convert(photos, YSBHPhoto.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_member_images;
   }
}

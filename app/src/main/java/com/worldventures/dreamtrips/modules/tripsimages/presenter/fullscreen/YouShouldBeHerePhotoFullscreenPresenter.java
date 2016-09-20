package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;

public class YouShouldBeHerePhotoFullscreenPresenter extends SocialFullScreenPresenter<YSBHPhoto, YouShouldBeHerePhotoFullscreenPresenter.View> {

   public YouShouldBeHerePhotoFullscreenPresenter(YSBHPhoto photo, TripImagesType type) {
      super(photo, type);
   }

   public interface View extends SocialFullScreenPresenter.View {}

}

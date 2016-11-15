package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

public class TripImageFullscreenPresenter extends SocialFullScreenPresenter<TripImage, TripImageFullscreenPresenter.View> {

   public TripImageFullscreenPresenter(TripImage photo, TripImagesType type) {
      super(photo, type);
   }

   public interface View extends SocialFullScreenPresenter.View {}

}

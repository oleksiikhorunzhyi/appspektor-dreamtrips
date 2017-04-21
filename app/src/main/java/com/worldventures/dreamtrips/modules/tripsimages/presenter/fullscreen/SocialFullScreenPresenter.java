package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

public class SocialFullScreenPresenter<T extends IFullScreenObject,
      PRESENTER_VIEW extends SocialFullScreenPresenter.View> extends FullScreenPresenter<T, PRESENTER_VIEW> {

   public SocialFullScreenPresenter(T photo, TripImagesType type) {
      super(photo, type);
   }

   @Override
   public void takeView(PRESENTER_VIEW view) {
      super.takeView(view);
   }

   public interface View extends FullScreenPresenter.View {}
}

package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;

import com.worldventures.dreamtrips.core.ui.fragment.BaseImagePresenter;
import com.worldventures.dreamtrips.core.ui.fragment.ImageBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;


public class TripImagePresenter extends BaseImagePresenter<IFullScreenObject> {

   public TripImagePresenter(ImageBundle<IFullScreenObject> bundle) {
      super(bundle);
   }

   @Override
   protected Uri createUri(IFullScreenObject image, int width, int height) {
      return Uri.parse(image.getFSImage().getUrl(width, height));
   }
}


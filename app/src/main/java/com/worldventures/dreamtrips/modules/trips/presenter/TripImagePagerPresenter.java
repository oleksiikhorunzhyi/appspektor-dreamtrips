package com.worldventures.dreamtrips.modules.trips.presenter;

import android.net.Uri;

import com.worldventures.core.model.ImagePathHolder;
import com.worldventures.core.utils.ImageUtils;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImagePresenter;
import com.worldventures.dreamtrips.core.ui.fragment.ImageBundle;


public class TripImagePagerPresenter extends BaseImagePresenter<ImagePathHolder> {

   public TripImagePagerPresenter(ImageBundle<ImagePathHolder> bundle) {
      super(bundle);
   }

   @Override
   protected Uri createUri(ImagePathHolder image, int width, int height) {
      return Uri.parse(ImageUtils.getParametrizedUrl(image.getImagePath(), width, height));
   }
}


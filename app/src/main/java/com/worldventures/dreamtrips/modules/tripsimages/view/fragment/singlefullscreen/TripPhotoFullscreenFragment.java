package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.TripImageFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ScaleImageView;

import butterknife.InjectView;

@Layout(R.layout.fragment_fullscreen_trip_photo)
public class TripPhotoFullscreenFragment extends FullScreenPhotoFragment<TripImageFullscreenPresenter, TripImage> {

   @InjectView(R.id.iv_image) ScaleImageView ivImage;

   @Override
   protected TripImageFullscreenPresenter createPresenter(Bundle savedInstanceState) {
      return new TripImageFullscreenPresenter((TripImage) getArgs().getPhoto(), getArgs().getType());
   }
}

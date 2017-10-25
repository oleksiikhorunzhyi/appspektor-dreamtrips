package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment;


import android.os.Bundle;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.TripImagesViewPagerPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesFullscreenArgs;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class TripImagesFullscreenFragment extends BaseImageViewPagerFragment<TripImagesViewPagerPresenter, TripImagesFullscreenArgs>
      implements TripImagesViewPagerPresenter.View {

   @Override
   protected TripImagesViewPagerPresenter createPresenter(Bundle savedInstanceState) {
      return new TripImagesViewPagerPresenter(getArgs());
   }
}

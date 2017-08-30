package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;
import com.worldventures.dreamtrips.core.ui.fragment.ImagePathHolder;
import com.worldventures.dreamtrips.modules.trips.presenter.TripImagePagerPresenter;

@Layout(R.layout.fragment_image_details)
public class TripImagePagerFragment extends BaseImageFragment<ImagePathHolder> implements TripImagePagerPresenter.View {

   @Override
   protected TripImagePagerPresenter createPresenter(Bundle savedInstanceState) {
      return new TripImagePagerPresenter(getArgs());
   }

}
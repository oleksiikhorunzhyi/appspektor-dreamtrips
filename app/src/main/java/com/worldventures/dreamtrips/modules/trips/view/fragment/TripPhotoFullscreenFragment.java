package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ImageryView;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import butterknife.InjectView;

@Layout(R.layout.fragment_fullscreen_trip_photo)
public class TripPhotoFullscreenFragment extends BaseFragmentWithArgs<Presenter, TripImage> {

   @InjectView(R.id.iv_image) ImageryView imageryView;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      imageryView.loadImage(getArgs().getUrl());
   }

   @Override
   protected Presenter createPresenter(Bundle savedInstanceState) {
      return new Presenter();
   }
}

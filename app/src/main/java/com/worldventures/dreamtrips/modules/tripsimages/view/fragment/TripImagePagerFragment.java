package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagePresenter;

@Layout(R.layout.fragment_image_details)
public class TripImagePagerFragment extends BaseImageFragment<IFullScreenObject> implements TripImagePresenter.View {

   @Override
   protected TripImagePresenter createPresenter(Bundle savedInstanceState) {
      return new TripImagePresenter(getArgs());
   }

}
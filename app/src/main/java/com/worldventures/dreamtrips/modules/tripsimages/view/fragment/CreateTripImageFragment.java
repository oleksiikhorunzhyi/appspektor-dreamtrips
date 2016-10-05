package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateEntityFragment;

@Layout(R.layout.layout_post)
public class CreateTripImageFragment extends CreateEntityFragment<CreateEntityPresenter> {

   @Override
   protected CreateEntityPresenter createPresenter(Bundle savedInstanceState) {
      return new CreateEntityPresenter();
   }

   @Override
   public void setText(String text) {
      // don't need to attach post description field
   }

   @Override
   protected Route getRoute() {
      return Route.PHOTO_CREATE;
   }
}

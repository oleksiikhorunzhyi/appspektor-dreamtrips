package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateEntityFragment;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreateTripImagePresenter;

@Layout(R.layout.layout_post)
public class CreateTripImageFragment extends CreateEntityFragment<CreateTripImagePresenter> {

    @Override
    protected CreateTripImagePresenter createPresenter(Bundle savedInstanceState) {
        return new CreateTripImagePresenter();
    }

    @Override
    public void setText(String text) {
        // don't need to attach post description field
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

package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateEntityFragment;

import java.util.ArrayList;
import java.util.List;

@Layout(R.layout.layout_create_trip_image)
public class CreateTripImageFragment extends CreateEntityFragment<CreateEntityPresenter> {

    @Override
    protected CreateEntityPresenter createPresenter(Bundle savedInstanceState) {
        return new CreateEntityPresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attachImages();
    }

    protected void attachImages() {
        getPresenter().attachImages(getImages(), getImagesType());
    }

    private int getImagesType() {
        return getArgs() != null ? getArgs().getImageType() : -1;
    }

    private List<ChosenImage> getImages() {
        return getArgs() != null ? getArgs().getImages() : new ArrayList<>();
    }

    @Override
    protected Route getRoute() {
        return Route.PHOTO_CREATE;
    }
}

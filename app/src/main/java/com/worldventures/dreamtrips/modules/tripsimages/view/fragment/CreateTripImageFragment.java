package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateEntityFragment;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreateTripImagePresenter;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

@Layout(R.layout.layout_post)
public class CreateTripImageFragment extends CreateEntityFragment<CreateTripImagePresenter> {

    @State
    boolean imageFromArgsAlreadyAttached;

    @Override
    protected CreateTripImagePresenter createPresenter(Bundle savedInstanceState) {
        return new CreateTripImagePresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attachImages();
    }

    protected void attachImages() {
        if (!imageFromArgsAlreadyAttached) {
            getPresenter().attachImages(getImages(), getImagesType());
            imageFromArgsAlreadyAttached = true;
        }
    }

    private int getImagesType() {
        return getArgs() != null ? getArgs().getImageType() : -1;
    }

    private List<ChosenImage> getImages() {
        return getArgs() != null ? getArgs().getImages() : new ArrayList<>();
    }

    @Override
    protected void onPhotoCancel() {
        showMediaPicker();
    }

    @Override
    protected Route getRoute() {
        return Route.PHOTO_CREATE;
    }
}

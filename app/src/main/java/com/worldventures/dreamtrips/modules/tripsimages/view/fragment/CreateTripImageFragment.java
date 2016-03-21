package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateEntityFragment;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

import static com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter.MEDIA_REQUEST_ID;

@Layout(R.layout.layout_create_trip_image)
public class CreateTripImageFragment extends CreateEntityFragment<CreateEntityPresenter> {

    @State
    boolean imageFromArgsAlreadyAttached;

    @Override
    protected CreateEntityPresenter createPresenter(Bundle savedInstanceState) {
        return new CreateEntityPresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attachImages();
        post.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forRemoval()
                        .fragmentManager(getChildFragmentManager())
                        .containerId(R.id.picker_container)
                        .build());
            else name.requestFocus();
        });
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
        router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
                .backStackEnabled(true)
                .fragmentManager(getChildFragmentManager())
                .containerId(R.id.picker_container)
                .data(new PickerBundle(MEDIA_REQUEST_ID))
                .build());
    }

    @Override
    protected Route getRoute() {
        return Route.PHOTO_CREATE;
    }
}

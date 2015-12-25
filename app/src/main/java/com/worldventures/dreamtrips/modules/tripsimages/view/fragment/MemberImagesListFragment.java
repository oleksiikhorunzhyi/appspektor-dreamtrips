package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MemberImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_account_images_list)
public class MemberImagesListFragment<P extends MemberImagesPresenter> extends TripImagesListFragment<P> implements MemberImagesPresenter.View {

    @InjectView(R.id.fab_photo)
    protected FloatingActionButton fabPhoto;
    @InjectView(R.id.photo_picker)
    PhotoPickerLayout photoPickerLayout;

    private boolean isVisible;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        inject(photoPickerLayout);
        setupPicker();
    }

    @OnClick(R.id.fab_photo)
    public void actionPhoto() {
        photoPickerLayout.showPanel();

        if (this instanceof AccountImagesListFragment) {
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MY_IMAGES);
        } else {
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MEMBER_IMAGES);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isVisible = isVisibleToUser;

        setupPicker();

        super.setUserVisibleHint(isVisibleToUser);
    }

    private void setupPicker() {
        if (photoPickerLayout == null) return;
        //
        photoPickerLayout.setup(getChildFragmentManager(), false, isVisible);
        photoPickerLayout.setOnDoneClickListener(chosenImages -> getPresenter()
                .attachImages(chosenImages, PickImageDelegate.REQUEST_PICK_PICTURE));
        hidePhotoPicker();
    }

    @Override
    public void hidePhotoPicker() {
        photoPickerLayout.hidePanel();
    }
}

package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.UserImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_account_images_list)
public class UsersImagesListFragment<P extends UserImagesPresenter> extends TripImagesListFragment<P> implements UserImagesPresenter.View {

    @InjectView(R.id.fab_photo)
    protected FloatingActionButton fabPhoto;
    @InjectView(R.id.photo_picker)
    PhotoPickerLayout photoPickerLayout;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        photoPickerLayout.setup(this, false);
        photoPickerLayout.setOnDoneClickListener(chosenImages -> getPresenter().attachImages(chosenImages, PickImageDelegate.REQUEST_PICK_PICTURE));
    }

    @OnClick(R.id.fab_photo)
    public void actionPhoto() {
        photoPickerLayout.showPanel();

        if (this instanceof AccountImagesListFragment){
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MY_IMAGES);
        } else {
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MEMBER_IMAGES);
        }
    }

    @Override
    public void hidePhotoPicker() {
        photoPickerLayout.hidePanel();
    }
}

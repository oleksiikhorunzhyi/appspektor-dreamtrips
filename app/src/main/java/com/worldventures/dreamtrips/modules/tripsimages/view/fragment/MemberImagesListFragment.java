package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.feed.bundle.CreatePostBundle;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MembersImagesPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_account_images_list)
public class MemberImagesListFragment<P extends MembersImagesPresenter> extends TripImagesListFragment<P>
        implements MembersImagesPresenter.View {

    @Inject
    BackStackDelegate backStackDelegate;

    @InjectView(R.id.fab_photo)
    protected FloatingActionButton fabPhoto;
    @InjectView(R.id.photo_picker)
    PhotoPickerLayout photoPickerLayout;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        inject(photoPickerLayout);
        setupPicker();
    }

    @Override
    public void onResume() {
        super.onResume();
        backStackDelegate.setListener(this::onBackPressed);
    }

    @Override
    public void onPause() {
        super.onPause();
        backStackDelegate.setListener(null);
    }

    private boolean onBackPressed() {
        if (photoPickerLayout.isPanelVisible()) {
            photoPickerLayout.hidePanel();
            return true;
        }
        return false;
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
        super.setUserVisibleHint(isVisibleToUser);

        setupPicker();
    }

    @Override
    public void attachImages(List<ChosenImage> photos, int requestType) {
        if (photos.size() == 0) return;
        //
        hidePhotoPicker();
        openCreatePhoto(new CreatePostBundle(photos, requestType));
    }

    private void setupPicker() {
        if (photoPickerLayout == null) return;
        //
        photoPickerLayout.setup(getChildFragmentManager(), false, getUserVisibleHint());
        photoPickerLayout.setOnDoneClickListener(this::attachImages);
        hidePhotoPicker();
    }

    private void hidePhotoPicker() {
        photoPickerLayout.hidePanel();
    }

    private void openCreatePhoto(CreatePostBundle bundle) {
        router.moveTo(Route.PHOTO_CREATE, NavigationConfigBuilder.forRemoval()
                .containerId(R.id.container_details_floating)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .build());
        router.moveTo(Route.PHOTO_CREATE, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .containerId(R.id.container_details_floating)
                .data(bundle)
                .build());
    }
}

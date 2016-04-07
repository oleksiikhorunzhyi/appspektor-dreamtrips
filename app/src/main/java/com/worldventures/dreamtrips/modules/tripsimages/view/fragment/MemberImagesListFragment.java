package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MembersImagesPresenter;

import java.util.List;

import butterknife.OnClick;

@Layout(R.layout.fragment_account_images_list)
public class MemberImagesListFragment<P extends MembersImagesPresenter> extends TripImagesListFragment<P>
        implements MembersImagesPresenter.View {

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser)
            hidePhotoPicker();
    }

    @OnClick(R.id.fab_photo)
    public void actionPhoto() {
        showPhotoPicker();
        //
        if (this instanceof AccountImagesListFragment) {
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MY_IMAGES);
        } else {
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MEMBER_IMAGES);
        }
    }

    @Override
    public void attachImages(List<ChosenImage> photos, int requestType) {
        hidePhotoPicker();
        openCreatePhoto(new CreateEntityBundle(photos, requestType));
    }

    private void showPhotoPicker() {
        router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(getChildFragmentManager())
                .containerId(R.id.picker_container)
                .data(new PickerBundle(getPresenter().getMediaRequestId()))
                .build());
    }

    private void hidePhotoPicker() {
        if (router == null) return;
        //
        router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forRemoval()
                .containerId(R.id.picker_container)
                .fragmentManager(getChildFragmentManager())
                .build());
    }

    private void openCreatePhoto(CreateEntityBundle bundle) {
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

package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MembersImagesPresenter;

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

    @OnClick(R.id.fab_photo)
    public void actionPhoto() {
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
                .containerId(R.id.container_details_floating)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .build());
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .containerId(R.id.container_details_floating)
                .data(new PostBundle(null, PostBundle.PHOTO, true))
                .build());

        if (this instanceof AccountImagesListFragment) {
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MY_IMAGES);
        } else {
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MEMBER_IMAGES);
        }
    }
}

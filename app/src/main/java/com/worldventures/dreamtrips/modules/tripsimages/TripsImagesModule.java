package com.worldventures.dreamtrips.modules.tripsimages;

import android.content.Context;

import com.messenger.ui.fragment.MessageImageFullscreenFragment;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetUserPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreateTripImagePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.EditPhotoTagsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.InspireMePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.PhotoEditPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.YSBHPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.AccountImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.BucketFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.InspirationFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MembersImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.SocialImageFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.TripImageFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.YouShouldBeHerePhotoFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoUploadCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.AccountImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.EditPhotoTagsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoWrapperFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenTripImageFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.MemberImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagePagerFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.BucketPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.InspirePhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.SocialImageFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.TripPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.YSBHPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.EditPhotoTagsCallback;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                TripImagesTabsPresenter.class,
                TripImagesListPresenter.class,
                InspireMePresenter.class,
                AccountImagesPresenter.class,
                MembersImagesPresenter.class,
                YSBHPresenter.class,
                FullScreenPhotoFragment.class,
                TripImagePresenter.class,

                FullScreenPresenter.class,
                BucketFullscreenPresenter.class,
                InspirationFullscreenPresenter.class,

                TripImagesTabsFragment.class,
                TripImagesListFragment.class,
                TripImagesTabsFragment.class,
                TripImagePagerFragment.class,
                PhotoEditPresenter.class,
                FullScreenTripImageFragment.class,
                PhotoCell.class,
                PhotoUploadCell.class,

                FullScreenPhotoWrapperFragment.class,
                GetUserPhotosQuery.class,
                AccountImagesListFragment.class,
                MemberImagesListFragment.class,

                InspirePhotoFullscreenFragment.class,
                YSBHPhotoFullscreenFragment.class,
                YouShouldBeHerePhotoFullscreenPresenter.class,
                TripPhotoFullscreenFragment.class,
                BucketPhotoFullscreenFragment.class,
                SocialImageFullscreenPresenter.class,
                SocialImageFullscreenFragment.class,

                InspirationFullscreenPresenter.class,
                TripImageFullscreenPresenter.class,

                MessageImageFullscreenFragment.class,
                MessageImageFullscreenPresenter.class,

                EditPhotoTagsPresenter.class,
                EditPhotoTagsFragment.class,

                CreateTripImagePresenter.class,
        },
        complete = false,
        library = true
)
public class TripsImagesModule {

    public static final String TRIP_IMAGES = Route.TRIP_TAB_IMAGES.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideTripImagesComponent() {
        return new ComponentDescription(TRIP_IMAGES, R.string.trip_images, R.string.trip_images, R.drawable.ic_trip_images, TripImagesTabsFragment.class);
    }

    @Provides
    DrawableUtil provideDrawableUtil(Context context) {
        return new DrawableUtil(context);
    }

    @Provides
    @Singleton
    EditPhotoTagsCallback provideEditPhotoTagsCallback() {
        return new EditPhotoTagsCallback();
    }

}

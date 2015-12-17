package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.utils.events.ImageClickedEvent;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripDetailsPresenter;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripDetailsBundle;
import com.worldventures.dreamtrips.modules.trips.view.util.TripDetailsViewDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_feed_trip_details)
public class TripWithSocialDetailsFragment extends BaseFragmentWithArgs<TripDetailsPresenter, TripDetailsBundle>
        implements TripDetailsPresenter.View {

    @InjectView(R.id.feed_trip_inner_toolbar)
    Toolbar feedTripInnerToolbar;

    TripDetailsViewDelegate tripDetailsViewDelegate;

    @Override
    protected TripDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new TripDetailsPresenter(getArgs().tripModel());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        tripDetailsViewDelegate = new TripDetailsViewDelegate(rootView);
        feedTripInnerToolbar.inflateMenu(R.menu.menu_detailed_trip);
        tripDetailsViewDelegate.initMenuItems(feedTripInnerToolbar.getMenu());

        feedTripInnerToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_like:
                    getPresenter().likeTrip();
                    return true;
                case R.id.action_add_to_bucket:
                    getPresenter().addTripToBucket();
                    return true;
            }
            return true;
        });
        tripDetailsViewDelegate.initGalleryData(getChildFragmentManager(), getPresenter().getFilteredImages());
    }

    public void onEvent(ImageClickedEvent event) {
        getPresenter().onItemClick(tripDetailsViewDelegate.getCurrentActivePhotoPosition());
    }

    @Override
    public void setContent(List<ContentItem> contentItems) {
    }

    @Override
    public void hideBookIt() {
    }

    @Override
    public void openFullscreen(FullScreenImagesBundle data) {
        NavigationBuilder.create().with(activityRouter)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(data).move(Route.FULLSCREEN_PHOTO_LIST);
    }

    @Override
    public void openBookIt(String url) {
        NavigationBuilder.create().with(activityRouter).data(new UrlBundle(url)).move(Route.BOOK_IT);
    }

    @OnClick(R.id.openTripDetails)
    void openTripDetails() {
        NavigationBuilder.create()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .with(activityRouter)
                .data(new TripDetailsBundle(getPresenter().getTrip()))
                .attach(Route.DETAILED_TRIP);
    }

    @Override
    public void setup(TripModel tripModel) {
        tripDetailsViewDelegate.initTripData(tripModel, getPresenter().getAccount());
    }
}

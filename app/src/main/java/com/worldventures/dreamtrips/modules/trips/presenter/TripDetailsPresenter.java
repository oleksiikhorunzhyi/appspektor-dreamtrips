package com.worldventures.dreamtrips.modules.trips.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.trips.api.GetTripDetailsQuery;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;
import java.util.List;

public class TripDetailsPresenter extends BaseTripPresenter<TripDetailsPresenter.View> {

    private List<TripImage> filteredImages;

    public TripDetailsPresenter(TripModel trip) {
        super(trip);
        filteredImages = new ArrayList<>();
        filteredImages.addAll(trip.getFilteredImages());
    }

    @Override
    public void onInjected() {
        super.onInjected();
        TrackingHelper.trip(String.valueOf(trip.getTripId()), getAccountUserId());
        loadTripDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (trip.isSoldOut() || (!appSessionHolder.get().get().getUser().isPlatinum()
                && trip.isPlatinum())) {
            view.hideBookIt();
        }
    }

    public List<TripImage> getFilteredImages() {
        return filteredImages;
    }

    public void actionBookIt() {
        TrackingHelper.bookIt(String.valueOf(trip.getTripId()), getAccountUserId());
        UserSession userSession = appSessionHolder.get().get();

        AppConfig.URLS urls = userSession.getGlobalConfig().getUrls();
        AppConfig.URLS.Config config = urls.getProduction();

        String url = config.getBookingPageURL()
                .replace(AppConfig.TRIP_ID, trip.getTripId())
                .replace(AppConfig.USER_ID, userSession.getUser().getUsername())
                .replace(AppConfig.TOKEN, userSession.getLegacyApiToken());
        view.openBookIt(url);
    }

    @Override
    public void onMenuPrepared() {
        if (view != null && trip != null) {
            view.setup(trip);
        }
    }

    public void loadTripDetails() {
        doRequest(new GetTripDetailsQuery(trip.getTripId()), tripDetails -> {
            view.setContent(tripDetails.getContent());
            TrackingHelper.tripInfo(String.valueOf(trip.getTripId()), getAccountUserId());
        });
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.setContent(null);
    }

    public void onItemClick(int position) {
        FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                .position(position)
                .userId(trip.getOwner().getId())
                .route(Route.TRIP_PHOTO_FULLSCREEN)
                .type(TripImagesListFragment.Type.FIXED)
                .fixedList(new ArrayList<>(filteredImages))
                .build();

        view.openFullscreen(data);
    }

    public interface View extends BaseTripPresenter.View {
        void setContent(List<ContentItem> contentItems);

        void hideBookIt();

        void openFullscreen(FullScreenImagesBundle data);

        void openBookIt(String url);
    }
}

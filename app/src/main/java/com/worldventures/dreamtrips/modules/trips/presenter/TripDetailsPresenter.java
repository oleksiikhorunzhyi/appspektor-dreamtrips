package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.command.TripsInteractor;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class TripDetailsPresenter extends BaseTripPresenter<TripDetailsPresenter.View> {

    @Inject TripsInteractor tripsInteractor;

    private List<TripImage> filteredImages;

    public TripDetailsPresenter(TripModel trip) {
        super(trip);
        filteredImages = new ArrayList<>();
        filteredImages.addAll(trip.getFilteredImages());
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.trip(String.valueOf(trip.getTripId()), getAccountUserId());
        loadTripDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getAccount().isMember()) {
            view.hideBookIt();
            view.showSignUp();
        } else if (trip.isSoldOut()
                || (!getAccount().isPlatinum() && trip.isPlatinum())) {
            view.hideBookIt();
        }
    }

    public List<TripImage> getFilteredImages() {
        return filteredImages;
    }

    public void actionBookIt() {
        TrackingHelper.actionBookIt(TrackingHelper.ATTRIBUTE_BOOK_IT, trip.getTripId(), getAccountUserId());
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
        view.bindUntilDropView(tripsInteractor.detailsPipe()
                .createObservable(new GetTripDetailsCommand(trip.getTripId()))
                .observeOn(AndroidSchedulers.mainThread())
        ).subscribe(new ActionStateSubscriber<GetTripDetailsCommand>()
                .onSuccess(command -> {
                    TrackingHelper.tripInfo(String.valueOf(trip.getTripId()), getAccountUserId());
                    view.setContent(command.getResult().getContent());
                })
                .onFail((command, e) -> {
                    view.setContent(null);
                    view.informUser(command.getErrorMessage());
                }));
    }

    public void onItemClick(int position) {
        FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                .position(position)
                .route(Route.TRIP_PHOTO_FULLSCREEN)
                .type(TripImagesType.FIXED)
                .fixedList(new ArrayList<>(filteredImages))
                .build();

        view.openFullscreen(data);
    }

    public interface View extends BaseTripPresenter.View {
        void setContent(List<ContentItem> contentItems);

        void hideBookIt();

        void showSignUp();

        void openFullscreen(FullScreenImagesBundle data);

        void openBookIt(String url);
    }
}

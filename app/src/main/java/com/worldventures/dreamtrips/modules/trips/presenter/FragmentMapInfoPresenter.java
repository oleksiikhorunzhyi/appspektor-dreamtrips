package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.utils.events.InfoWindowSizeEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowInfoWindowEvent;
import com.worldventures.dreamtrips.core.utils.events.TripLikedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public class FragmentMapInfoPresenter extends Presenter<FragmentMapInfoPresenter.View> {

    private TripModel trip;

    public FragmentMapInfoPresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
    }

    private void setView() {
        view.setName(trip.getName());
        view.setPrice(trip.getPrice().toString());
        view.setDate(trip.getAvailabilityDates().toString());
        view.setImage(trip.getImageUrl("THUMB"));
        if (trip.getRewardsLimit() > 0) {
            view.setPoints(String.valueOf(trip.getRewardsLimit()));
        } else {
            view.setPointsInvisible();
        }
        view.setPlace(trip.getGeoLocation().getName());
        view.setLiked(trip.isLiked());
        view.setDescription(trip.getDescription());
        view.setFeatured(trip.isFeatured());
    }

    public TripModel getTrip() {
        return trip;
    }

    public void setTrip(TripModel trip) {
        this.trip = trip;
        setView();
    }

    public void onEvent(TripLikedEvent tripEvent) {
        if (tripEvent.getTrip().getId() == trip.getId()) {
            trip.setLiked(tripEvent.getTrip().isLiked());
            view.setLiked(trip.isLiked());
        }
    }


    public void sendOffset(int offset) {
        eventBus.post(new InfoWindowSizeEvent(offset));
    }

    public void onEvent(ShowInfoWindowEvent ev) {
        view.showLayout();
    }

    public void onClick() {
        activityRouter.openTripDetails(trip);
    }

    public interface View extends Presenter.View {
        void setName(String name);

        void setDate(String date);

        void setImage(String image);

        void setPrice(String price);

        void setPoints(String points);

        void setPlace(String place);

        void setLiked(boolean liked);

        void setDescription(String description);

        void showLayout();

        void setPointsInvisible();

        void setFeatured(boolean isFeatured);
    }
}

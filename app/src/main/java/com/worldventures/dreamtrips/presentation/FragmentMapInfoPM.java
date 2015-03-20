package com.worldventures.dreamtrips.presentation;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.utils.events.InfoWindowSizeEvent;
import com.worldventures.dreamtrips.utils.events.ShowInfoWindowEvent;
import com.worldventures.dreamtrips.utils.events.TripLikedEvent;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by Edward on 28.01.15.
 * presentation model for fragmentMapTripInfo
 */
public class FragmentMapInfoPM extends BasePresentation<FragmentMapInfoPM.View> {

    @Global
    @Inject
    EventBus eventBus;
    private Trip trip;

    public FragmentMapInfoPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        eventBus.register(this);
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

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
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

    public interface View extends BasePresentation.View {
        void setName(String name);

        void setDate(String вфе);

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

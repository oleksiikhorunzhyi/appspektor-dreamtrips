package com.worldventures.dreamtrips.presentation;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.utils.busevents.InfoWindowSizeEvent;
import com.worldventures.dreamtrips.utils.busevents.ShowInfoWindowEvent;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by Edward on 28.01.15.
 * presentation model for fragmentMapTripInfo
 */
@PresentationModel
public class FragmentMapInfoPM extends BasePresentation<FragmentMapInfoPM.View> {

    private Trip trip;

    @Global
    @Inject
    EventBus eventBus;

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
        view.setPoints(String.valueOf(trip.getRewardsLimit()));
        view.setPlace(trip.getGeoLocation().getName());
        view.setLiked(trip.isLiked());
        view.setDescription(trip.getDescription());
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        setView();
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
    }
}

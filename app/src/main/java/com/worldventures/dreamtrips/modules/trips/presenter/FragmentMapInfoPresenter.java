package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.utils.events.InfoWindowSizeEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowInfoWindowEvent;
import com.worldventures.dreamtrips.core.utils.events.TripLikedEvent;

public class FragmentMapInfoPresenter extends BaseTripPresenter<FragmentMapInfoPresenter.View> {

    @Override
    public void onResume() {
        super.onResume();
        view.setImage(trip.getThumb(context.getResources()));
    }

    public void onEvent(TripLikedEvent tripEvent) {
        if (tripEvent.getTrip().getTripId() == trip.getTripId()) {
            trip.setLiked(tripEvent.getTrip().isLiked());
            view.setLike(trip.isLiked());
        }
    }

    public void sendOffset(int offset) {
        eventBus.post(new InfoWindowSizeEvent(offset));
    }

    public void onEvent(ShowInfoWindowEvent ev) {
        view.showLayout();
    }

    public void onClick() {
        fragmentCompass.pop();
        activityRouter.openTripDetails(trip);
    }

    public interface View extends BaseTripPresenter.View {
        void showLayout();

        void setImage(String url);
    }
}

package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.utils.events.InfoWindowSizeEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowInfoWindowEvent;

public class MapTripInfoPresenter extends BaseTripPresenter<MapTripInfoPresenter.View> {

    @Override
    protected void initData() {
        super.initData();
        view.setImage(trip.getThumb(context.getResources()));
        view.setInBucket(trip.isInBucketList());
        view.setLike(trip.isLiked());
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

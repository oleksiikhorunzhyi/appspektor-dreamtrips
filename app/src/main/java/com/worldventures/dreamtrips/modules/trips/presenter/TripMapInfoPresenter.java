package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.utils.events.MapInfoReadyEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowMapInfoEvent;

public class TripMapInfoPresenter extends BaseTripPresenter<TripMapInfoPresenter.View> {

    @Override
    protected void initData() {
        super.initData();
        view.setImage(trip.getThumb(context.getResources()));
        view.setInBucket(trip.isInBucketList());
        view.setLike(trip.isLiked());
    }

    public void sendOffset(int offset) {
        eventBus.post(new MapInfoReadyEvent(offset));
    }

    public void onEvent(ShowMapInfoEvent ev) {
        view.showLayout();
    }

    public void onClick() {
        activityRouter.openTripDetails(trip);
    }

    public interface View extends BaseTripPresenter.View {
        void showLayout();

        void setImage(String url);
    }
}

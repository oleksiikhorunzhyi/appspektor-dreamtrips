package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.utils.events.MapInfoReadyEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripDetailsBundle;

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
        NavigationBuilder.create().data(new TripDetailsBundle(trip))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .with(activityRouter).move(Route.DETAILED_TRIP);
    }

    public interface View extends BaseTripPresenter.View {
        void showLayout();

        void setImage(String url);
    }
}

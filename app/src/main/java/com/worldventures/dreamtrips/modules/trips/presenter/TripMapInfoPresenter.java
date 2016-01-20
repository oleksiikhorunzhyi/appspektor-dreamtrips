package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.utils.events.MapInfoReadyEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

public class TripMapInfoPresenter extends BaseTripPresenter<TripMapInfoPresenter.View> {

    @Inject
    protected Presenter.TabletAnalytic tabletAnalytic;

    public TripMapInfoPresenter(TripModel trip) {
        super(trip);
    }

    @Override
    protected void initData() {
        super.initData();
        view.setImage(trip.getThumb(context.getResources()));
    }

    public void sendOffset(int offset) {
        eventBus.post(new MapInfoReadyEvent(offset));
    }

    public void onEvent(ShowMapInfoEvent ev) {
        view.showLayout();
    }

    public void onClick() {
        FeedDetailsBundle bundle = new FeedDetailsBundle(FeedItem.create(trip, appSessionHolder.get().get().getUser()));
        if (tabletAnalytic.isTabletLandscape()) {
            bundle.setSlave(true);
        }
        view.openDetails(bundle);
    }

    public interface View extends BaseTripPresenter.View {
        void showLayout();

        void setImage(String url);

        void openDetails(FeedDetailsBundle bundle);
    }
}

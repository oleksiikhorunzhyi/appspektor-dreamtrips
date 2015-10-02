package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountFeedQuery;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class FeedPresenter extends BaseFeedPresenter<FeedPresenter.View> {

    @Inject
    SnappyRepository db;
    //
    String circleId;

    @Override
    public void takeView(View view) {
        super.takeView(view);
    }

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        Circle filterCircle = db.getFilterCircle();
        if (filterCircle != null) circleId = filterCircle.getId();
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getRefreshFeedRequest(Date date) {
        return new GetAccountFeedQuery(date, circleId);
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getNextPageFeedRequest(Date date) {
        return new GetAccountFeedQuery(date, circleId);
    }

    public List<Circle> getFilterItems(){
        List<Circle> circles = db.getCircles();
        circles.add(Circle.all(context.getString(R.string.all)));
        return circles;
    }

    public void applyFilter(Circle selectedCircle){
        circleId = selectedCircle.getId();
        db.saveFilterCircle(selectedCircle);
        onRefresh();
    }

    public void onEventMainThread(HeaderCountChangedEvent event) {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    public void refreshRequestsCount() {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    public interface View extends BaseFeedPresenter.View {
        void setRequestsCount(int count);
    }
}

package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountFeedQuery;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class FeedPresenter extends BaseFeedPresenter<FeedPresenter.View> {

    @Inject
    SnappyRepository db;
    //
    Circle filterCircle;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setupAccount(appSessionHolder.get().get().getUser());
    }

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        filterCircle = db.getFilterCircle();
        if (filterCircle == null) filterCircle = createDefaultFilterCircle();
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getRefreshFeedRequest(Date date) {
        return new GetAccountFeedQuery(date, filterCircle.getId());
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getNextPageFeedRequest(Date date) {
        return new GetAccountFeedQuery(date, filterCircle.getId());
    }

    public List<Circle> getFilterCircles() {
        List<Circle> circles = db.getCircles();
        Collections.sort(circles);
        circles.add(0, createDefaultFilterCircle());
        return circles;
    }

    private Circle createDefaultFilterCircle() {
        return Circle.all(context.getString(R.string.all));
    }

    public Circle getAppliedFilterCircle() {
        return filterCircle;
    }

    public void applyFilter(Circle selectedCircle) {
        filterCircle = selectedCircle;
        db.saveFilterCircle(selectedCircle);
        onRefresh();
    }

    public void onEventMainThread(HeaderCountChangedEvent event) {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    public void refreshRequestsCount() {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    public void onUserClick() {
        openUser(appSessionHolder.get().get().getUser());
    }

    public interface View extends BaseFeedPresenter.View {
        void setRequestsCount(int count);

        void setupAccount(User user);
    }
}

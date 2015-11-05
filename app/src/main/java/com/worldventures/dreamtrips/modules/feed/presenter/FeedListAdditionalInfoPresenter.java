package com.worldventures.dreamtrips.modules.feed.presenter;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.events.UnfriendEvent;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.List;

import javax.inject.Inject;

public class FeedListAdditionalInfoPresenter extends FeedItemAdditionalInfoPresenter<FeedListAdditionalInfoPresenter.View> {

    @Inject
    SnappyRepository db;

    public FeedListAdditionalInfoPresenter(FeedAdditionalInfoBundle args) {
        super();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (view.isTabletLandscape()) {
            loadFriends(1);
            view.setCurrentCircle(getFilterCircle());
        }
    }

    public void loadFriends(int page) {
        doRequest(new GetFriendsQuery(getFilterCircle(), null, page, getPageSize()), users -> {
            if (page == 1) view.setFriends(users);
            else view.addFriends(users);
        });
    }

    public void circlePicked(Circle c) {
        db.saveFeedFriendPickedCircle(c);
        loadFriends(1);
    }

    public void onCircleFilterClicked() {
        List<Circle> circles = db.getCircles();
        circles.add(0, getDefaultCircleFilter());
        view.showCirclePicker(circles, getFilterCircle());
    }


    @NonNull
    private Circle getFilterCircle() {
        Circle filterCircle = db.getFeedFriendPickedCircle();
        return filterCircle == null ? getDefaultCircleFilter() : filterCircle;
    }

    private Circle getDefaultCircleFilter() {
        return Circle.all(context.getString(R.string.show_all));
    }

    public int getPageSize() {
        return 20;
    }

    public void onEvent(UnfriendEvent event) {
        view.removeFriend(event.getFriend());
    }

    public interface View extends FeedItemAdditionalInfoPresenter.View {

        void setFriends(@NonNull List<User> friends);

        void addFriends(@NonNull List<User> friends);

        void removeFriend(@NonNull User friend);

        void showCirclePicker(@NonNull List<Circle> circles, @NonNull Circle activeCircle);

        void setCurrentCircle(Circle currentCircle);
    }
}

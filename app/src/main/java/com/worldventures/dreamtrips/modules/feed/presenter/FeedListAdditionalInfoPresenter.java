package com.worldventures.dreamtrips.modules.feed.presenter;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.events.UnfriendEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.List;

import javax.inject.Inject;

public class FeedListAdditionalInfoPresenter extends FeedItemAdditionalInfoPresenter<FeedListAdditionalInfoPresenter.View> {

    @Inject
    SnappyRepository db;

    private int nextPage;

    public FeedListAdditionalInfoPresenter(FeedAdditionalInfoBundle args) {
        super(args);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (view.isTabletLandscape()) {
            nextPage = 1;
            loadFriends();
            view.setCurrentCircle(getFilterCircle());
        }
    }

    public void loadFriends() {
        doRequest(new GetFriendsQuery(getFilterCircle(), null, nextPage, getPageSize()), users -> {
            if (nextPage == 1) view.setFriends(users);
            else view.addFriends(users);
            this.nextPage++;
        });
    }

    public void circlePicked(Circle c) {
        nextPage = 1;
        db.saveFeedFriendPickedCircle(c);
        loadFriends();
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
        return Circle.all(context.getString(R.string.all_friends));
    }

    public int getPageSize() {
        return 20;
    }

    public void onEvent(UnfriendEvent event) {
        view.removeFriend(event.getFriend());
    }

    public void onEvent(UserClickedEvent event) {
        if (view.isVisibleOnScreen()) {
            view.openUser(new UserBundle(event.getUser()));
            eventBus.cancelEventDelivery(event);
        }
    }

    public interface View extends FeedItemAdditionalInfoPresenter.View {

        void setFriends(@NonNull List<User> friends);

        void addFriends(@NonNull List<User> friends);

        void removeFriend(@NonNull User friend);

        void showCirclePicker(@NonNull List<Circle> circles, @NonNull Circle activeCircle);

        void setCurrentCircle(Circle currentCircle);

        void openUser(UserBundle bundle);
    }
}

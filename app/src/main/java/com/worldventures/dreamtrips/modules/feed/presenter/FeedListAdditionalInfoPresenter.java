package com.worldventures.dreamtrips.modules.feed.presenter;

import android.support.annotation.NonNull;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.User;
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

    private int nextPage = 1;
    private int prevTotalItemCount = 0;
    private boolean loading = true;
    private boolean canLoadMore = true;

    public FeedListAdditionalInfoPresenter(User user) {
        super(user);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (view.isTabletLandscape()) {
            loadFriends();
            view.setCurrentCircle(getFilterCircle());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Circles interaction
    ///////////////////////////////////////////////////////////////////////////

    public void onCirclePicked(Circle c) {
        db.saveFeedFriendPickedCircle(c);
        reload();
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

    ///////////////////////////////////////////////////////////////////////////
    // Friends loading
    ///////////////////////////////////////////////////////////////////////////

    public void loadFriends() {
        loading = true;
        view.startLoading();
        doRequest(new GetFriendsQuery(getFilterCircle(), null, nextPage, getPageSize()), users -> {
            if (nextPage == 1) view.setFriends(users);
            else view.addFriends(users);
            canLoadMore = users.size() > 0;
            nextPage++;
            loading = false;
            view.finishLoading();
        });
    }

    public void reload() {
        nextPage = 1;
        prevTotalItemCount = 0;
        loadFriends();
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        loading = false;
        view.finishLoading();
    }

    public void onScrolled(int totalItemCount, int lastVisible) {
        if (totalItemCount > prevTotalItemCount) {
            prevTotalItemCount = totalItemCount;
        }
        if (!loading && canLoadMore && lastVisible >= totalItemCount - 1) {
            loadFriends();
        }
    }

    public int getPageSize() {
        return 100;
    }

    ///////////////////////////////////////////////////////////////////////////
    // User related events
    ///////////////////////////////////////////////////////////////////////////

    public void onEvent(UserClickedEvent event) {
        if (view.isVisibleOnScreen()) {
            view.openUser(new UserBundle(event.getUser()));
            eventBus.cancelEventDelivery(event);
        }
    }

    public void onEvent(UnfriendEvent event) {
        view.removeFriend(event.getFriend());
    }

    public interface View extends FeedItemAdditionalInfoPresenter.View {

        void startLoading();

        void finishLoading();

        void setFriends(@NonNull List<User> friends);

        void addFriends(@NonNull List<User> friends);

        void removeFriend(@NonNull User friend);

        void showCirclePicker(@NonNull List<Circle> circles, @NonNull Circle activeCircle);

        void setCurrentCircle(Circle currentCircle);

        void openUser(UserBundle bundle);
    }
}

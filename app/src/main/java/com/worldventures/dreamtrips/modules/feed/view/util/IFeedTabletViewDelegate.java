package com.worldventures.dreamtrips.modules.feed.view.util;

import android.view.View;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.List;

public interface IFeedTabletViewDelegate {
    void setRootView(View view);

    void setUser(User user, boolean withDetails);

    void setFriends(List<User> friends, Injector injector);

    void addFriends(List<User> friends);

    void setCircles(List<Circle> circles, int defCircleIndex);

    void setOnUserClick(ActionListener onUserClick);

    void setOnCreatePostClick(ActionListener onCreatePostClick);

    void setOnCirclePicked(CirclePickedListener onCirclePicked);

    void setOnSharePhotoClick(ActionListener onSharePhotoClick);

    void setRequestMoreUsersListener(RequestMoreUsersListener requestMoreUsersListener);

    void setOnSearchUserClick(ActionListener onSearchUserClick);

    interface ActionListener {
        void onAction();
    }

    interface CirclePickedListener {
        void onAction(Circle circle);
    }

    interface RequestMoreUsersListener {
        void needMore(int page, Circle circle);
    }
}

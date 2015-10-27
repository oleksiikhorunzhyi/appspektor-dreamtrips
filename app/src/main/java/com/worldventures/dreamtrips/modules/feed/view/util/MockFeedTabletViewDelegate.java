package com.worldventures.dreamtrips.modules.feed.view.util;

import android.view.View;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.List;

public class MockFeedTabletViewDelegate implements IFeedTabletViewDelegate {
    @Override
    public void setRootView(View view) {

    }

    @Override
    public void setUser(User user, boolean withDetails) {

    }

    @Override
    public void setCloseFriends(List<User> friends, Injector injector) {

    }

    @Override
    public void addCloseFriends(List<User> friends) {

    }

    @Override
    public void setOnUserClick(FeedTabletViewDelegate.ActionListener onUserClick) {

    }

    @Override
    public void setOnCreatePostClick(FeedTabletViewDelegate.ActionListener onCreatePostClick) {

    }

    @Override
    public void setOnCirclePicked(FeedTabletViewDelegate.CirclePickedListener onCirclePicked) {

    }

    @Override
    public void setRequestMoreUsersListener(RequestMoreUsersListener requestMoreUsersListener) {

    }

    @Override
    public void setCircles(List<Circle> circles, int defCircleIndex) {

    }

    @Override
    public void setOnSharePhotoClick(ActionListener onSharePhotoClick) {

    }
}

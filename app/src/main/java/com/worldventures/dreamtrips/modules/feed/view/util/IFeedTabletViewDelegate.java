package com.worldventures.dreamtrips.modules.feed.view.util;

import android.view.View;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

public interface IFeedTabletViewDelegate {
    void setRootView(View view);

    void setUser(User user, boolean withDetails);

    void setCloseFriends(List<User> friends, Injector injector);

    void setOnUserClick(FeedTabletViewDelegate.ViewClickListener onUserClick);

    void setOnCreatePostClick(FeedTabletViewDelegate.ViewClickListener onCreatePostClick);

    void setOnFriendsMoreClick(FeedTabletViewDelegate.ViewClickListener onFriendsMoreClick);
}

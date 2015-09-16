package com.worldventures.dreamtrips.modules.friends.presenter;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.api.SearchUsersQuery;
import com.worldventures.dreamtrips.modules.friends.events.QueryStickyEvent;

import java.util.ArrayList;

import icepick.Icicle;

public class FriendSearchPresenter extends BaseUserListPresenter<FriendSearchPresenter.View> {

    @Icicle
    String query;

    public FriendSearchPresenter() {
    }

    @Override
    protected Query<ArrayList<User>> getUserListQuery(int page) {
        return new SearchUsersQuery(query, page);
    }

    @Override
    protected void userStateChanged(User user) {
        view.finishLoading();
        users.remove(user);
        view.refreshUsers(users);
    }

    @Override
    public void onInjected() {
        super.onInjected();
        QueryStickyEvent event = eventBus.getStickyEvent(QueryStickyEvent.class);
        if (event != null) {
            query = event.getQuery();
            eventBus.removeStickyEvent(event);
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        circles = snappyRepository.getCircles();
    }

    public void setQuery(String query) {
        this.query = query;
        reload();
    }

    public String getQuery() {
        return query;
    }

    public interface View extends BaseUserListPresenter.View {
    }
}

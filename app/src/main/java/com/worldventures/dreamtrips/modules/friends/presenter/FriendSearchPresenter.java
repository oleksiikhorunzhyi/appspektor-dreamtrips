package com.worldventures.dreamtrips.modules.friends.presenter;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.api.SearchUsersQuery;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;

import java.util.ArrayList;

import icepick.State;

public class FriendSearchPresenter extends BaseUserListPresenter<FriendSearchPresenter.View> {

    @State
    String query;

    public FriendSearchPresenter(String query) {
        this.query = query;
    }

    @Override
    protected Query<ArrayList<User>> getUserListQuery(int page) {
        return new SearchUsersQuery(query, page,getPerPageCount());
    }

    @Override
    protected void userStateChanged(User user) {
        view.finishLoading();
        view.refreshUsers(users);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        circles = snappyRepository.getCircles();
    }

    public void setQuery(String query) {
        this.query = query;
        if (query.length() < 3) {
            if (users.size() > 0)
                onUsersLoaded(new ArrayList<>());
            return;
        }
        reload();
    }

    public void addUserRequest(User user) {
        eventBus.post(new AddUserRequestEvent(user));
    }

    @Override
    protected boolean isNeedPreload() {
        return false;
    }

    @Override
    public void scrolled(int totalItemCount, int lastVisible) {
        if (query.length() < 3) return;
        super.scrolled(totalItemCount, lastVisible);
    }

    public String getQuery() {
        return query;
    }

    public interface View extends BaseUserListPresenter.View {
    }
}

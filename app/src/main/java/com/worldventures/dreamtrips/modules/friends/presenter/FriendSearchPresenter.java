package com.worldventures.dreamtrips.modules.friends.presenter;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.api.SearchUsersQuery;

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
        reload();
    }

    public String getQuery() {
        return query;
    }

    public interface View extends BaseUserListPresenter.View {
    }
}

package com.worldventures.dreamtrips.modules.friends.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.api.GetUsersLikedEntityQuery;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;

import java.util.ArrayList;

public class UsersLikedItemPresenter extends BaseUserListPresenter<UsersLikedItemPresenter.View> {

    private String entityUid;

    public UsersLikedItemPresenter(UsersLikedEntityBundle bundle) {
        entityUid = bundle.getUid();
    }

    @Override
    protected Query<ArrayList<User>> getUserListQuery(int page) {
        return new GetUsersLikedEntityQuery(entityUid, page, getPerPageCount());
    }

    @Override
    protected void onUsersAdded(ArrayList<User> freshUsers) {
        super.onUsersAdded(updateUserCircles(freshUsers));
    }

    @Override
    protected void onUsersLoaded(ArrayList<User> freshUsers) {
        super.onUsersLoaded(updateUserCircles(freshUsers));
    }

    private ArrayList<User> updateUserCircles(ArrayList<User> freshUsers){
        Queryable.from(freshUsers).forEachR(friend -> friend.setCircles(circles));
        return freshUsers;
    }

    @Override
    protected void userStateChanged(User user) {
        view.finishLoading();

        int index = users.indexOf(user);
        if (index != -1) {
            users.remove(index);
            users.add(index, user);
            view.refreshUsers(users);
        }
    }

    public interface View extends BaseUserListPresenter.View {

    }
}

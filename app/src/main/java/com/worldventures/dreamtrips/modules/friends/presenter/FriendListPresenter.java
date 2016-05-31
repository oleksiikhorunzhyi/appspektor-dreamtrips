package com.worldventures.dreamtrips.modules.friends.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.events.ReloadFriendListEvent;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class FriendListPresenter extends BaseUserListPresenter<FriendListPresenter.View> {

    @State
    Circle selectedCircle;
    @State
    String query;
    @State
    int position = 0;

    @Inject
    SnappyRepository snappyRepository;

    @Override
    protected Query<ArrayList<User>> getUserListQuery(int page) {
        return new GetFriendsQuery(selectedCircle, query, page, getPerPageCount());
    }

    @Override
    public void onInjected() {
        super.onInjected();
        Collections.sort(circles);
        circles.add(0, Circle.all(context.getString(R.string.show_all)));
        query = "";
    }

    public void onFilterClicked() {
        view.showFilters(circles, position);
    }

    public void reloadWithFilter(int position) {
        this.position = position;
        selectedCircle = position != 0 ? circles.get(position) : null;
        reload();
    }

    @Override
    protected void userStateChanged(User user) {
        view.finishLoading();
        users.remove(user);
        view.refreshUsers(users);
    }

    public void setQuery(String query) {
        int previousLength = this.query.length();
        this.query = query;
        if (query.length() < 3 && (previousLength < query.length() || previousLength < 3))
            return;
        //
        reload();
    }

    public void onEvent(ReloadFriendListEvent event) {
        reload();
    }

    public String getQuery() {
        return query;
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        if (view != null) view.finishLoading();
    }

    public interface View extends BaseUserListPresenter.View {

        void showFilters(List<Circle> circles, int selectedPosition);

        void openFriendPrefs(UserBundle userBundle);
    }
}

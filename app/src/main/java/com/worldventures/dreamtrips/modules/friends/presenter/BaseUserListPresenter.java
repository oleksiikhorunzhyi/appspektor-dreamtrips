package com.worldventures.dreamtrips.modules.friends.presenter;

import com.innahema.collections.query.functions.Action1;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.AddUserRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.DeleteRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.UnfriendCommand;
import com.worldventures.dreamtrips.modules.friends.events.AcceptRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.CancelRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.HideRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.OpenFriendPrefsEvent;
import com.worldventures.dreamtrips.modules.friends.events.RejectRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.ReloadFriendListEvent;
import com.worldventures.dreamtrips.modules.friends.events.RemoveUserEvent;
import com.worldventures.dreamtrips.modules.friends.events.UnfriendEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.event.FriendGroupRelationChangedEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class BaseUserListPresenter<T extends BaseUserListPresenter.View> extends Presenter<T> {

    private static final int PER_PAGE = 20;

    private int previousTotal = 0;
    private boolean loading = true;

    protected List<User> users = new ArrayList<>();

    protected List<Circle> circles;

    @Inject
    SnappyRepository snappyRepository;

    @Override
    public void takeView(T view) {
        super.takeView(view);
        reload();
        circles = snappyRepository.getCircles();
    }

    @Override
    public void onResume() {
        RemoveUserEvent event = eventBus.getStickyEvent(RemoveUserEvent.class);
        if (event != null) {
            eventBus.removeStickyEvent(event);
            userStateChanged(event.getUser());
        }
    }

    public void reload() {
        resetLazyLoadFields();
        view.startLoading();
        doRequest(getUserListQuery(1), this::onUsersLoaded);
    }

    protected void onUsersLoaded(ArrayList<User> freshUsers) {
        users.clear();
        users.addAll(freshUsers);
        view.refreshUsers(users);
        view.finishLoading();
    }

    protected void onUsersAdded(ArrayList<User> freshUsers) {
        users.addAll(freshUsers);
        view.refreshUsers(users);
        view.finishLoading();
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        if (view != null) view.finishLoading();
    }

    protected abstract Query<ArrayList<User>> getUserListQuery(int page);

    public void scrolled(int totalItemCount, int lastVisible) {
        if (totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
        }
        if (!loading
                && lastVisible == totalItemCount - 1
                && totalItemCount % PER_PAGE == 0) {
            view.startLoading();
            doRequest(getUserListQuery(users.size() / PER_PAGE + 1), this::onUsersAdded);
            loading = true;
        }
    }

    private void resetLazyLoadFields() {
        previousTotal = 0;
        loading = false;
    }

    //////////////////////
    /// events
    //////////////////////

    public void onEvent(AcceptRequestEvent event) {
        if (view.isVisibleOnScreen()) {
            view.showAddFriendDialog(circles, position -> {
                view.startLoading();
                doRequest(new ActOnRequestCommand(event.getUser().getId(),
                                ActOnRequestCommand.Action.CONFIRM.name(),
                                circles.get(position).getId()),
                        object -> {
                            eventBus.post(new ReloadFriendListEvent());
                            User user = event.getUser();
                            user.setRelationship(User.Relationship.FRIEND);
                            userActionSucceed(user);
                        });
            });
        }
    }

    public void onEvent(CancelRequestEvent event) {
        if (view.isVisibleOnScreen()) {
            view.startLoading();
            doRequest(new DeleteRequestCommand(event.getUser().getId()),
                    object -> {
                        User user = event.getUser();
                        user.setRelationship(User.Relationship.NONE);
                        userActionSucceed(user);
                    });
        }
    }

    public void onEvent(HideRequestEvent event) {
        if (view.isVisibleOnScreen()) {
            view.startLoading();
            doRequest(new DeleteRequestCommand(event.getUser().getId()),
                    object -> {
                        User user = event.getUser();
                        user.setRelationship(User.Relationship.NONE);
                        userActionSucceed(user);
                    });
        }
    }

    public void onEvent(RejectRequestEvent event) {
        if (view.isVisibleOnScreen()) {
            view.startLoading();
            doRequest(new ActOnRequestCommand(event.getUser().getId(),
                            ActOnRequestCommand.Action.REJECT.name()),
                    object -> {
                        User user = event.getUser();
                        user.setRelationship(User.Relationship.NONE);
                        userActionSucceed(user);
                    });
        }
    }

    public void onEvent(UnfriendEvent event) {
        if (view.isVisibleOnScreen())
            unfriend(event.getFriend());
    }

    public void onEvent(OpenFriendPrefsEvent event) {
        view.openFriendPrefs(new UserBundle(event.getFriend()));
    }

    public void onEvent(AddUserRequestEvent event) {
        view.showAddFriendDialog(circles, arg -> {
            addFriend(event.getUser(), circles.get(arg));
        });
    }

    public void onEvent(FriendGroupRelationChangedEvent event) {
        for (User friend : users) {
            if (friend.getId() == event.getFriend().getId()) {
                switch (event.getState()) {
                    case REMOVED:
                        friend.getCircleIds().remove(event.getCircle().getId());
                        break;
                    case ADDED:
                        friend.getCircleIds().add(event.getCircle().getId());
                        break;
                }
                friend.setCircles(circles);
                view.refreshUsers(users);
                break;
            }
        }
    }

    private void userActionSucceed(User user) {
        if (view != null) {
            view.finishLoading();
            userStateChanged(user);
        }
    }

    public void onEvent(UserClickedEvent event) {
        view.openUser(new UserBundle(event.getUser()));
    }

    protected abstract void userStateChanged(User user);

    private void unfriend(User user) {
        view.startLoading();
        doRequest(new UnfriendCommand(user.getId()), object -> {
            user.setRelationship(User.Relationship.NONE);
            userActionSucceed(user);
        });
    }

    private void addFriend(User user, Circle circle) {
        view.startLoading();
        doRequest(new AddUserRequestCommand(user.getId(), circle),
                jsonObject -> {
                    user.setRelationship(User.Relationship.OUTGOING_REQUEST);
                    userStateChanged(user);
                });
    }

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        void refreshUsers(List<User> users);

        void openFriendPrefs(UserBundle userBundle);

        void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectAction);

        void openUser(UserBundle userBundle);
    }
}
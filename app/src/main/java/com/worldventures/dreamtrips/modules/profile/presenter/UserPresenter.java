package com.worldventures.dreamtrips.modules.profile.presenter;

import com.innahema.collections.query.functions.Action1;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.api.GetUserTimelineQuery;
import com.worldventures.dreamtrips.modules.feed.api.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.AddUserRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.UnfriendCommand;
import com.worldventures.dreamtrips.modules.friends.events.OpenFriendPrefsEvent;
import com.worldventures.dreamtrips.modules.friends.events.RemoveUserEvent;
import com.worldventures.dreamtrips.modules.friends.events.UnfriendEvent;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.profile.api.GetPublicProfileQuery;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.event.FriendGroupRelationChangedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnAcceptRequestEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnAddFriendEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnRejectRequestEvent;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class UserPresenter extends ProfilePresenter<UserPresenter.View, User> {

    private int notificationId;
    private boolean acceptFriend;

    @Inject
    NotificationDelegate notificationDelegate;

    public UserPresenter(UserBundle userBundle) {
        super(userBundle.getUser());
        this.notificationId = userBundle.getNotificationId();
        this.acceptFriend = userBundle.isAcceptFriend();
        userBundle.resetNotificationId();
        userBundle.resetAcceptFriend();
    }

    @Override
    public void onInjected() {
        super.onInjected();
        notificationDelegate.cancel(user.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (notificationId != UserBundle.NO_NOTIFICATION) {
            doRequest(new MarkNotificationAsReadCommand(notificationId), aVoid -> {
            });
        }
        if (acceptFriend) {
            acceptClicked();
            acceptFriend = false;
        }
    }

    @Override
    protected void loadProfile() {
        view.startLoading();
        doRequest(new GetPublicProfileQuery(user), this::onProfileLoaded);
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getRefreshFeedRequest(Date date) {
        return new GetUserTimelineQuery(user.getId());
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getNextPageFeedRequest(Date date) {
        return new GetUserTimelineQuery(user.getId(), date);
    }

    public void addFriendClicked() {
        User.Relationship userRelationship = user.getRelationship();
        if (userRelationship == null) return;

        switch (userRelationship) {
            case REJECT:
            case NONE:
                view.showAddFriendDialog(circles, this::addAsFriend);
                break;
            case FRIEND:
                view.showFriendDialog(user);
                break;
        }
    }

    private void unfriend() {
        view.startLoading();
        doRequest(new UnfriendCommand(user.getId()), object -> {
            if (view != null) {
                view.finishLoading();
                user.unfriend();
                view.notifyUserChanged();
                eventBus.postSticky(new RemoveUserEvent(user));
            }
        });
    }

    public void acceptClicked() {
        view.showAddFriendDialog(circles, this::accept);
    }

    public void rejectClicked() {
        reject();
    }

    private void addAsFriend(int position) {
        view.startLoading();
        Circle circle = circles.get(position);
        doRequest(new AddUserRequestCommand(user.getId(), circle),
                jsonObject -> {
                    user.setRelationship(User.Relationship.OUTGOING_REQUEST);
                    view.finishLoading();
                    view.notifyUserChanged();
                });
    }

    private void reject() {
        view.startLoading();
        doRequest(new ActOnRequestCommand(user.getId(),
                        ActOnRequestCommand.Action.REJECT.name()),
                object -> {
                    view.finishLoading();
                    user.setRelationship(User.Relationship.REJECT);
                    view.notifyUserChanged();
                });
    }

    private void accept(int position) {
        Circle circle = snappyRepository.getCircles().get(position);
        doRequest(new ActOnRequestCommand(user.getId(),
                        ActOnRequestCommand.Action.CONFIRM.name(),
                        circle.getId()),
                object -> {
                    user.setRelationship(User.Relationship.FRIEND);
                    view.notifyUserChanged();
                });
    }


    public void onEvent(UnfriendEvent event) {
        unfriend();
    }

    public void onEvent(OpenFriendPrefsEvent event) {
        view.openFriendPrefs(new UserBundle(user));
    }


    public void onEvent(FriendGroupRelationChangedEvent event) {
        if (user.getId() == event.getFriend().getId()) {
            switch (event.getState()) {
                case REMOVED:
                    user.getCircles().remove(event.getCircle());
                    break;
                case ADDED:
                    user.getCircles().add(event.getCircle());
                    break;
            }
            view.notifyUserChanged();
        }
    }


    @Override
    public void openBucketList() {
        NavigationBuilder
                .create()
                .data(new ForeignBucketTabsBundle(user))
                .with(activityRouter)
                .move(Route.FOREIGN_BUCKET_TABS);
    }

    @Override
    public void openTripImages() {
        NavigationBuilder
                .create()
                .with(activityRouter)
                .data(new TripsImagesBundle(TripImagesListFragment.Type.MEMBERS_IMAGES, user.getId()))
                .move(Route.LIST_IMAGES);
    }

    public void onEvent(OnAcceptRequestEvent e) {
        acceptClicked();
    }

    public void onEvent(OnRejectRequestEvent e) {
        rejectClicked();
    }

    public void onEvent(OnAddFriendEvent e) {
        addFriendClicked();
    }

    public interface View extends ProfilePresenter.View {

        void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectAction);

        void showFriendDialog(User user);

        void openFriendPrefs(UserBundle userBundle);
    }
}

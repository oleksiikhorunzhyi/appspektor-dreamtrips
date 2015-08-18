package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.innahema.collections.query.functions.Action1;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.api.GetUserFeedQuery;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;
import com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.AddUserRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.GetCirclesQuery;
import com.worldventures.dreamtrips.modules.friends.api.UnfriendCommand;
import com.worldventures.dreamtrips.modules.friends.events.OpenFriendPrefsEvent;
import com.worldventures.dreamtrips.modules.friends.events.RemoveUserEvent;
import com.worldventures.dreamtrips.modules.friends.events.UnfriendEvent;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.profile.api.GetPublicProfileQuery;
import com.worldventures.dreamtrips.modules.profile.event.FriendGroupRelationChangedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnAcceptRequestEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnAddFriendEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnRejectRequestEvent;

import java.util.ArrayList;
import java.util.List;

public class UserPresenter extends ProfilePresenter<UserPresenter.View, User> {

    public UserPresenter(Bundle args) {
        super(args.getParcelable(ProfileModule.EXTRA_USER));
    }

    @Override
    protected void loadProfile() {
        view.startLoading();
        doRequest(new GetPublicProfileQuery(user), this::onProfileLoaded);
    }

    @Override
    protected SpiceRequest<ArrayList<ParentFeedModel>> getRefreshRequest() {
        return new GetUserFeedQuery(user.getId(), 0);
    }

    @Override
    protected SpiceRequest<ArrayList<ParentFeedModel>> getNextPageRequest(int page) {
        return new GetUserFeedQuery(user.getId(), page);
    }

    public void addFriendClicked() {
        if (user.getRelationship() != null) {
            if (user.getRelationship().equals(User.Relationship.NONE)
                    || user.getRelationship().equals(User.Relationship.REJECT))
                view.showAddFriendDialog(circles, this::addAsFriend);
            else if (user.getRelationship().equals(User.Relationship.FRIEND))
                view.showFriendDialog(user);
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

    private void openFriendPrefs() {
        activityRouter.openFriendPrefs(user);
    }

    public void acceptClicked() {
        view.showAddFriendDialog(circles, this::accept);
    }

    public void rejectClicked() {
        reject();
    }

    private void addAsFriend(int position) {
        view.startLoading();
        if (circles.isEmpty()) {
            view.startLoading();
            doRequest(new GetCirclesQuery(), circles -> {
                view.finishLoading();
                saveCircles(circles);
                addAsFriend(position);
            });
        } else {
            Circle circle = circles.get(position);
            doRequest(new AddUserRequestCommand(user.getId(), circle),
                    jsonObject -> {
                        user.setRelationship(User.Relationship.OUTGOING_REQUEST);
                        view.finishLoading();
                        view.notifyUserChanged();
                    });
        }
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
        openFriendPrefs();
    }


    public void onEvent(FriendGroupRelationChangedEvent event) {
        if (user.getId() == event.getFriend().getId()) {
            switch (event.getState()) {
                case REMOVED:
                    user.getCircleIds().remove(event.getCircle().getId());
                    break;
                case ADDED:
                    user.getCircleIds().add(event.getCircle().getId());
                    break;
            }
            user.setCircles(snappyRepository.getCircles());
            view.notifyUserChanged();
        }
    }


    @Override
    public void openBucketList() {
    }

    @Override
    public void openTripImages() {
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
    }
}

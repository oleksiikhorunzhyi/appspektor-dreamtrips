package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.innahema.collections.query.functions.Action1;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.api.GetUserFeedQuery;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.AddUserRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.GetCirclesQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.profile.api.GetPublicProfileQuery;

import java.util.ArrayList;
import java.util.List;

public class UserPresenter extends ProfilePresenter<UserPresenter.View> {

    public UserPresenter(Bundle args) {
        super(args.getParcelable(ProfileModule.EXTRA_USER));
    }

    @Override
    protected void loadProfile() {
        view.startLoading();
        doRequest(new GetPublicProfileQuery(user), this::onProfileLoaded);
    }

    @Override
    protected SpiceRequest<ArrayList<BaseFeedModel>> getRefreshRequest() {
        return new GetUserFeedQuery(user.getId(), 0);
    }

    @Override
    protected SpiceRequest<ArrayList<BaseFeedModel>> getNextPageRequest(int page) {
        return new GetUserFeedQuery(user.getId(), page);
    }

    @Override
    protected void setUserProfileInfo() {
        super.setUserProfileInfo();
        view.setSocial(user.isSocialEnabled());
        view.setIsFriend(false);
        if (user.getRelationship() != null) {
            switch (user.getRelationship()) {
                case User.RELATION_FRIEND:
                    view.setIsFriend(true);
                    view.hideFriendRequest();
                    break;
                case User.RELATION_OUTGOING_REQUEST:
                    view.setWaiting();
                    view.hideFriendRequest();
                    break;
                case User.RELATION_INCOMING_REQUEST:
                    view.setRespond();
                    view.showFriendRequest(user.getFirstName());
                    break;
                default:
                    view.hideFriendRequest();
                    break;
            }
        }
    }

    public void addFriendClicked() {
        if (user.getRelationship() != null
                && (user.getRelationship().equals(User.RELATION_NONE)
                || user.getRelationship().equals(User.RELATION_REJECT)))
            view.showAddFriendDialog(circles, this::addAsFriend);
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
                        view.finishLoading();
                        view.setWaiting();
                    });
        }
    }

    private void reject() {
        view.startLoading();
        doRequest(new ActOnRequestCommand(user.getId(),
                        ActOnRequestCommand.Action.REJECT.name()),
                object -> {
                    view.finishLoading();
                    view.setIsFriend(false);
                    view.hideFriendRequest();
                });
    }

    private void accept(int position) {
        Circle circle = snappyRepository.getCircles().get(position);
        doRequest(new ActOnRequestCommand(user.getId(),
                        ActOnRequestCommand.Action.CONFIRM.name(),
                        circle.getId()),
                object -> {
                    view.setIsFriend(true);
                    view.hideFriendRequest();
                });
    }


    @Override
    public void openBucketList() {
        //TODO
        view.informUser(R.string.coming_soon);
    }

    @Override
    public void openTripImages() {
        //TODO
        view.informUser(R.string.coming_soon);
    }

    public interface View extends ProfilePresenter.View {

        void showFriendRequest(String name);

        void hideFriendRequest();

        void setIsFriend(boolean isFriend);

        void setRespond();

        void setWaiting();

        void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectAction);
    }
}

package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.innahema.collections.query.functions.Action1;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.AddUserRequestCommand;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.profile.api.GetPublicProfileQuery;

import java.util.List;

import icepick.Icicle;

public class UserPresenter extends ProfilePresenter<UserPresenter.View> {

    List<Circle> circles;

    public UserPresenter(Bundle args) {
        super(args.getParcelable(ProfileModule.EXTRA_USER));
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        circles = snappyRepository.getCircles();
    }

    @Override
    protected void loadProfile() {
        view.startLoading();
        doRequest(new GetPublicProfileQuery(user), this::onProfileLoaded);
    }

    @Override
    protected void setUserProfileInfo() {
        super.setUserProfileInfo();
        //view.setSocial(user.isSocialEnabled());
        view.setSocial(true);
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
        Circle circle = circles.get(position);
        doRequest(new AddUserRequestCommand(user.getId(), circle),
                jsonObject -> view.setWaiting());
    }

    private void reject() {
        doRequest(new ActOnRequestCommand(user.getId(),
                        ActOnRequestCommand.Action.REJECT.name()),
                object -> {
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
    }

    @Override
    public void openTripImages() {
        //TODO
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

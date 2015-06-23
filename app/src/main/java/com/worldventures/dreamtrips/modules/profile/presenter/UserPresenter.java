package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.api.AddUserRequestCommand;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;

public class UserPresenter extends ProfilePresenter<UserPresenter.View> {

    public UserPresenter(Bundle args) {
        super(args.getParcelable(ProfileModule.EXTRA_USER));
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setIsFriend(false);
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

    @Override
    protected void loadProfile() {

    }

    public void addFriendClicked() {
        if (user.getRelationship().equals(User.RELATION_NONE))
            view.showAddFriendDialog(user.getFullName());
    }

    public void addAsFriend() {
        Circle circle = snappyRepository.getCircles().get(0);
        doRequest(new AddUserRequestCommand(user.getId(), circle),
                jsonObject -> onSuccess());
    }

    private void onSuccess() {
        view.setWaiting();
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

        void showAddFriendDialog(String name);

        void setIsFriend(boolean isFriend);

        void setRespond();

        void setWaiting();
    }
}

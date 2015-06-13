package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.profile.ProfileModule;

public class UserPresenter extends ProfilePresenter<UserPresenter.View> {

    public UserPresenter(Bundle args) {
        super(args.getParcelable(ProfileModule.EXTRA_USER));
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        //TODO check has user sent a friend request
        view.showFriendRequest(user.getFirstName());
        //TODO add friend check
        boolean isFriend = false;
        view.setIsFriend(isFriend);
    }

    @Override
    protected void loadProfile() {

    }

    public void addFriendClicked() {
        view.showAddFriendDialog(user.getFullName());
    }

    public void addAsFriend() {
        view.setIsFriend(true);
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
    }
}

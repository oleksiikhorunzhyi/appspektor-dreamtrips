package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

public class ProfileActivityPresenter extends Presenter<ProfileActivityPresenter.View> {
    private User user;

    public ProfileActivityPresenter(UserBundle bundle) {
        user = bundle.getUser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
            User account = getAccount();
            if (user == null || user.equals(account)) {
                view.openAccountProfile();
            } else {
                view.openForeignProfile(user);
            }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public interface View extends Presenter.View {
        void openAccountProfile();

        void openForeignProfile(User user);
    }

}

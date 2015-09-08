package com.worldventures.dreamtrips.modules.profile.presenter;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

public class ProfileWrapperPresenter extends Presenter<ProfileWrapperPresenter.View> {

    public ProfileWrapperPresenter() {
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        UserBundle args = view.getArgs();
        if (args != null) {
            User user = args.getUser();
            User account = getAccount();
            if (user == null || user.equals(account)) {
                view.openAccountProfile();
            } else {
                view.openForeignProfile(user);
            }
        }
    }

    public interface View extends Presenter.View {
        void openAccountProfile();

        void openForeignProfile(User user);

        UserBundle getArgs();
    }

}

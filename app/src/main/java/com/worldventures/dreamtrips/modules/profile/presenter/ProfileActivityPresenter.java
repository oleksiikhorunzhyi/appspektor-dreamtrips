package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.techery.spares.ui.routing.BaseRouter;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;

public class ProfileActivityPresenter extends Presenter<Presenter.View> {
    private User user;

    public ProfileActivityPresenter(Bundle bundle) {
        user = bundle.getBundle(BaseRouter.EXTRA_BUNDLE).getParcelable(ProfileModule.EXTRA_USER);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (user.equals(getAccount())) {
            fragmentCompass.replace(Route.MY_PROFILE);
        } else {
            Bundle args = new Bundle();
            args.putParcelable(ProfileModule.EXTRA_USER, user);
            fragmentCompass.replace(Route.PROFILE, args);
        }
    }
}

package com.worldventures.dreamtrips.modules.profile.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.event.BackPressedMessage;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfileWrapperPresenter;

@Layout(R.layout.fragment_profile_wrapper)
public class ProfileWrapperFragment extends BaseFragmentWithArgs<ProfileWrapperPresenter, UserBundle> implements ProfileWrapperPresenter.View {

    @Override
    protected ProfileWrapperPresenter createPresenter(Bundle savedInstanceState) {
        return new ProfileWrapperPresenter();
    }

    @Override
    public void openAccountProfile() {
        fragmentCompass.disableBackStack();
        NavigationBuilder.create().with(fragmentCompass)
                .move(Route.ACCOUNT_PROFILE);
        clearArgs();
    }

    @Override
    public void openForeignProfile(User user) {
        fragmentCompass.disableBackStack();
        NavigationBuilder.create().with(fragmentCompass).data(new UserBundle(user))
                .move(Route.FOREIGN_PROFILE);
        clearArgs();
    }
}


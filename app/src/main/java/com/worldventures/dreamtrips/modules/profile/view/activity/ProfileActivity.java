package com.worldventures.dreamtrips.modules.profile.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfileActivityPresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_profile)
public class ProfileActivity extends BaseFragmentWithArgs<ProfileActivityPresenter, UserBundle> implements ProfileActivityPresenter.View {

    @Override
    protected ProfileActivityPresenter createPresenter(Bundle savedInstanceState) {
        return new ProfileActivityPresenter(getArgs());
    }

    @Override
    public void openAccountProfile() {
        NavigationBuilder.create().with(fragmentCompass)
                .move(Route.ACCOUNT_PROFILE);
    }

    @Override
    public void openForeignProfile(User user) {
        NavigationBuilder.create().with(fragmentCompass).data(new UserBundle(user))
                .move(Route.FOREIGN_PROFILE);

    }
}

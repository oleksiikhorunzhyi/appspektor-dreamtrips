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
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfileActivityPresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_profile)
public class ProfileActivity extends BaseFragmentWithArgs<ProfileActivityPresenter, UserBundle> implements ProfileActivityPresenter.View {

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @InjectView(R.id.container_details_floating)
    View detailsFloatingContainer;

/*TODO
    @Override
    public void onBackPressed() {
        if (!handleComponentChange()) super.onBackPressed();
    }
*/

    boolean handleComponentChange() {
        if (detailsFloatingContainer != null && detailsFloatingContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removePost();
            detailsFloatingContainer.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    protected ProfileActivityPresenter createPresenter(Bundle savedInstanceState) {
        return new ProfileActivityPresenter(getArgs());
    }

    @Override
    public void openAccountProfile() {
        fragmentCompass.setSupportFragmentManager(getChildFragmentManager());
        NavigationBuilder.create().with(fragmentCompass).move(Route.MY_PROFILE);
    }

    @Override
    public void openForeignProfile(User user) {
        fragmentCompass.setSupportFragmentManager(getChildFragmentManager());
        NavigationBuilder.create().with(fragmentCompass).data(new UserBundle(user)).move(Route.PROFILE);

    }
}

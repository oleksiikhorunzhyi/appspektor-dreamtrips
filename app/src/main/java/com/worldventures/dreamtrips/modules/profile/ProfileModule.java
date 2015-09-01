package com.worldventures.dreamtrips.modules.profile;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.profile.presenter.AccountPresenter;
import com.worldventures.dreamtrips.modules.profile.presenter.FriendPreferencesPresenter;
import com.worldventures.dreamtrips.modules.profile.presenter.FriendPrefsWrapperPresenter;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfileActivityPresenter;
import com.worldventures.dreamtrips.modules.profile.presenter.UserPresenter;
import com.worldventures.dreamtrips.modules.profile.view.activity.FriendPrefsWrapperActivity;
import com.worldventures.dreamtrips.modules.profile.view.activity.ProfileActivity;
import com.worldventures.dreamtrips.modules.profile.view.cell.FriendPrefGroupCell;
import com.worldventures.dreamtrips.modules.profile.view.cell.ProfileCell;
import com.worldventures.dreamtrips.modules.profile.view.cell.ReloadFeedCell;
import com.worldventures.dreamtrips.modules.profile.view.fragment.AccountFragment;
import com.worldventures.dreamtrips.modules.profile.view.fragment.FriendPreferenceFragment;
import com.worldventures.dreamtrips.modules.profile.view.fragment.UserFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                UserFragment.class,
                UserPresenter.class,
                AccountFragment.class,
                AccountPresenter.class,
                ProfileActivityPresenter.class,
                ProfileActivity.class,
                FriendPreferenceFragment.class,
                FriendPreferencesPresenter.class,
                FriendPrefGroupCell.class,
                FriendPrefsWrapperActivity.class,
                FriendPrefsWrapperPresenter.class,
                ProfileCell.class,
                ReloadFeedCell.class,
        },
        complete = false,
        library = true
)
public class ProfileModule {

    public static final String MY_PROFILE = Route.MY_PROFILE.name();
    public static final String EXTRA_USER = "user";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideProfileComponent() {
        return new ComponentDescription(MY_PROFILE, 0, R.string.my_profile, R.drawable.ic_profile,
                AccountFragment.class);
    }
}

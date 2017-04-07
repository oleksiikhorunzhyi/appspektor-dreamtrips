package com.worldventures.dreamtrips.modules.profile;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendPreferencesPresenter;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendPreferenceFragment;
import com.worldventures.dreamtrips.modules.profile.presenter.AccountPresenter;
import com.worldventures.dreamtrips.modules.profile.presenter.UserPresenter;
import com.worldventures.dreamtrips.modules.profile.view.cell.FriendPrefGroupCell;
import com.worldventures.dreamtrips.modules.profile.view.cell.ProfileCell;
import com.worldventures.dreamtrips.modules.profile.view.cell.ReloadFeedCell;
import com.worldventures.dreamtrips.modules.profile.view.fragment.AccountFragment;
import com.worldventures.dreamtrips.modules.profile.view.fragment.UserFragment;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            UserFragment.class,
            UserPresenter.class,
            AccountFragment.class,
            AccountPresenter.class,
            FriendPreferenceFragment.class,
            FriendPreferencesPresenter.class,
            FriendPrefGroupCell.class,
            ProfileCell.class,
            ReloadFeedCell.class,
      },
      complete = false,
      library = true)
public class ProfileModule {

   public static final String ACCOUNT_PROFILE = "ACCOUNT_PROFILE";

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideProfileComponent() {
      return new ComponentDescription.Builder()
            .key(ACCOUNT_PROFILE)
            .navMenuTitle(R.string.my_profile)
            .icon(R.drawable.ic_profile)
            .skipGeneralToolbar(true)
            .fragmentClass(AccountFragment.class)
            .shouldFinishMainActivity(true)
            .build();
   }
}

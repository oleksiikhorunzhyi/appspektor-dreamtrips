package com.worldventures.dreamtrips.modules.profile;

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
public class ProfileActivityModule {
}

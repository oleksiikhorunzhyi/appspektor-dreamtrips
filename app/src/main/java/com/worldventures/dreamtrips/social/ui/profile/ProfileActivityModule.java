package com.worldventures.dreamtrips.social.ui.profile;

import com.worldventures.dreamtrips.social.ui.friends.presenter.FriendPreferencesPresenter;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.FriendPreferenceFragment;
import com.worldventures.dreamtrips.social.ui.profile.presenter.AccountPresenter;
import com.worldventures.dreamtrips.social.ui.profile.presenter.UserPresenter;
import com.worldventures.dreamtrips.social.ui.profile.view.cell.ProfileCell;
import com.worldventures.dreamtrips.social.ui.profile.view.fragment.AccountFragment;
import com.worldventures.dreamtrips.social.ui.profile.view.fragment.UserFragment;

import dagger.Module;

@Module(
      injects = {
            UserFragment.class,
            UserPresenter.class,
            AccountFragment.class,
            AccountPresenter.class,
            FriendPreferenceFragment.class,
            FriendPreferencesPresenter.class,
            ProfileCell.class,
      },
      complete = false,
      library = true)
public class ProfileActivityModule {
}

package com.worldventures.dreamtrips.modules.friends;

import com.messenger.storage.dao.UsersDAO;
import com.techery.spares.utils.UserStatusAdapter;
import com.techery.spares.utils.UserStatusDaoAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendSearchPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendsMainPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.MutualFriendsPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.RequestsPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.UsersLikedItemPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.FeedFriendCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.FriendCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.MutualFriendCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestHeaderCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserSearchCell;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendListFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendSearchFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendsMainFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.MutualFriendsFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.RequestsFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.UsersLikedItemFragment;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            FriendSearchFragment.class, FriendSearchPresenter.class, FriendsMainPresenter.class,
            FriendListFragment.class, FriendsMainFragment.class, FriendListPresenter.class,
            FriendCell.class, UserCell.class, FeedFriendCell.class, MutualFriendCell.class,

            UsersLikedItemPresenter.class, UsersLikedItemFragment.class,

            MutualFriendsFragment.class, MutualFriendsPresenter.class,

            RequestsFragment.class, RequestsPresenter.class,

            RequestCell.class, RequestHeaderCell.class, UserSearchCell.class,

            SmartAvatarView.class},
      complete = false,
      library = true)
public class FriendsModule {

   @Provides
   UserStatusAdapter provideUserStatusAdapter(UsersDAO usersDao) {
      return new UserStatusDaoAdapter(usersDao);
   }
}

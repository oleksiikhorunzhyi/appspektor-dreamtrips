package com.worldventures.dreamtrips.social.ui.friends;

import com.worldventures.dreamtrips.social.ui.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.social.ui.friends.presenter.FriendSearchPresenter;
import com.worldventures.dreamtrips.social.ui.friends.presenter.FriendsMainPresenter;
import com.worldventures.dreamtrips.social.ui.friends.presenter.MutualFriendsPresenter;
import com.worldventures.dreamtrips.social.ui.friends.presenter.RequestsPresenter;
import com.worldventures.dreamtrips.social.ui.friends.presenter.UsersLikedItemPresenter;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.FeedFriendCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.FriendCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.MutualFriendCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.RequestCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.UserCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.UserSearchCell;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.FriendListFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.FriendSearchFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.FriendsMainFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.MutualFriendsFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.RequestsFragment;
import com.worldventures.dreamtrips.social.ui.friends.view.fragment.UsersLikedItemFragment;

import dagger.Module;

@Module(
      injects = {
            FriendSearchFragment.class,
            FriendSearchPresenter.class,
            FriendsMainPresenter.class,
            FriendListFragment.class,
            FriendsMainFragment.class,
            FriendListPresenter.class,
            FriendCell.class,
            UserCell.class,
            FeedFriendCell.class,
            MutualFriendCell.class,
            UsersLikedItemPresenter.class,
            UsersLikedItemFragment.class,
            MutualFriendsFragment.class,
            MutualFriendsPresenter.class,
            RequestsFragment.class,
            RequestsPresenter.class,
            RequestCell.class,
            UserSearchCell.class
      },
      complete = false,
      library = true)
public class FriendsModule {
}

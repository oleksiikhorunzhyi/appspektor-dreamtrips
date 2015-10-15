package com.worldventures.dreamtrips.modules.friends;

import com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendSearchPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendsMainPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.RequestsPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.UsersLikedItemPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.CloseFriendCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.FriendCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestHeaderCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserSearchCell;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendListFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendSearchFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendsMainFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.RequestsFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.UsersLikedItemFragment;

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
                CloseFriendCell.class,

                UsersLikedItemPresenter.class,
                UsersLikedItemFragment.class,


                RequestsFragment.class,
                RequestsPresenter.class,

                RequestCell.class,
                RequestHeaderCell.class,
                UserSearchCell.class,
        },
        complete = false,
        library = true
)
public class FriendsModule {

}

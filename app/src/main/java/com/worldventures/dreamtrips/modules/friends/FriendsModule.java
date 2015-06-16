package com.worldventures.dreamtrips.modules.friends;

import com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendsMainPresenter;
import com.worldventures.dreamtrips.modules.friends.view.activity.FriendsActivity;
import com.worldventures.dreamtrips.modules.friends.view.cell.FriendCell;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendListFragment;

import dagger.Module;

@Module(
        injects = {
                FriendsActivity.class,
                FriendsMainPresenter.class,
                FriendListFragment.class,
                FriendListPresenter.class,
                FriendCell.class,

        },
        complete = false,
        library = true
)
public class FriendsModule {


}

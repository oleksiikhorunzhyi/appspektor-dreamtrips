package com.worldventures.dreamtrips.modules.friends;

import com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendsMainPresenter;
import com.worldventures.dreamtrips.modules.friends.view.activity.FriendsActivity;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserWrapperCell;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendListFragment;

import dagger.Module;

@Module(
        injects = {
                FriendsActivity.class,
                FriendsMainPresenter.class,
                FriendListFragment.class,
                FriendListPresenter.class,
                UserWrapperCell.class,

        },
        complete = false,
        library = true
)
public class FriendsModule {


}

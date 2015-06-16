package com.worldventures.dreamtrips.modules.friends;

import com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendsMainPresenter;
import com.worldventures.dreamtrips.modules.friends.presenter.RequestsPresenter;
import com.worldventures.dreamtrips.modules.friends.view.activity.FriendsActivity;
import com.worldventures.dreamtrips.modules.friends.view.cell.FriendCell;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendListFragment;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestHeaderCell;
import com.worldventures.dreamtrips.modules.friends.view.fragment.RequestsFragment;

import dagger.Module;

@Module(
        injects = {
                FriendsActivity.class,
                FriendsMainPresenter.class,
                FriendListFragment.class,
                FriendListPresenter.class,
                FriendCell.class,

                RequestsFragment.class,
                RequestsPresenter.class,

                RequestCell.class,
                RequestHeaderCell.class,

        },
        complete = false,
        library = true
)
public class FriendsModule {


}

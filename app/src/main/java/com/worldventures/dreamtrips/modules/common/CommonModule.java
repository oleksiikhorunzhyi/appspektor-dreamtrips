package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.SimpleStreamPlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.adapter.MyDraggableSwipeableItemAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerFragment;

import dagger.Module;

@Module(
        injects = {
                ActivityPresenter.class,
                LaunchActivityPresenter.class,
                MainActivityPresenter.class,
                NavigationDrawerPresenter.class,
                Presenter.class,
                SharePresenter.class,

                LaunchActivity.class,
                MainActivity.class,
                PlayerActivity.class,
                ShareActivity.class,
                SimpleStreamPlayerActivity.class,
                FilterableArrayListAdapter.class,
                MyDraggableSwipeableItemAdapter.class,
                NavigationDrawerFragment.class,
                NavigationDrawerFragment.class,
                NavigationDrawerAdapter.class,
        },
        complete = false,
        library = true
)
public class CommonModule {
}

package com.worldventures.dreamtrips.modules.bucketlist;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketListPopularActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketHeaderCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPopularCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketItemEditFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListPopuralFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                BucketItemEditPresenter.class,
                BucketPopularTabsPresenter.class,
                BucketItemEditFragment.class,
                BucketTabsPresenter.class,
                BucketListPresenter.class,
                BucketListPopularActivity.class,
                BucketPopularPresenter.class,
                BucketListPopuralFragment.class,
                BucketTabsFragment.class,
                BucketPopularTabsFragment.class,
                BucketListFragment.class,
                BucketHeaderCell.class,
                BucketItemCell.class,
                BucketPopularCell.class,
                AutoCompleteAdapter.class,
        },
        complete = false,
        library = true
)
public class BucketListModule {

    public static final String BUCKETLIST = "bucketlist";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideBucketListComponent() {
        return new ComponentDescription(BUCKETLIST, R.string.bucket_list, R.drawable.ic_bucket_lists, BucketTabsFragment.class);
    }
}

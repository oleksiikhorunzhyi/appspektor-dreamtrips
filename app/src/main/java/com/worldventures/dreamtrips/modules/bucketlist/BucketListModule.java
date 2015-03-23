package com.worldventures.dreamtrips.modules.bucketlist;


import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListEditActivityPM;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPopularPM;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListQuickInputPM;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularTabsFragmentPM;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsFragmentPM;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketHeaderCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPopularCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketQuickCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListPopuralFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListQuickInputFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

import dagger.Module;

@Module(
        injects = {
                BucketListQuickInputPM.class,
                BucketPopularTabsFragmentPM.class,
                BucketListQuickInputFragment.class,
                BucketTabsFragmentPM.class,
                BucketListPresenter.class,
                BucketListEditActivity.class,
                BucketListEditActivityPM.class,
                BucketListPopularPM.class,
                BucketListPopuralFragment.class,
                BucketTabsFragment.class,
                BucketPopularTabsFragment.class,
                BucketListFragment.class,
                BucketHeaderCell.class,
                BucketItemCell.class,
                BucketQuickCell.class,
                BucketPopularCell.class,

        },
        complete = false,
        library = true
)
public class BucketListModule {

}

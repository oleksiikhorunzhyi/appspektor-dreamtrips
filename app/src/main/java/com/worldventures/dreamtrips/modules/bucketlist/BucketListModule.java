package com.worldventures.dreamtrips.modules.bucketlist;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketTabPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.BucketItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketAddPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemStaticCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCellForDetails;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCellForeign;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoUploadCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPopularCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketPhotosView;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketItemEditFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListPopuralFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketTabsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.FixedPhotoFsPresenter;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                BucketItemEditPresenter.class,
                BucketPopularTabsPresenter.class,
                BucketItemEditFragment.class,
                BucketTabsPresenter.class,
                BucketListPresenter.class,
                BucketPopularPresenter.class,
                BucketListPopuralFragment.class,
                BucketTabsFragment.class,
                BucketPopularTabsFragment.class,
                BucketListFragment.class,
                BucketItemCell.class,
                BucketPopularCell.class,
                BucketDetailsFragment.class,
                BucketItemDetailsPresenter.class,
                AutoCompleteAdapter.class,
                BucketItemAdapter.class,
                IgnoreFirstItemAdapter.class,
                BucketAddPhotoCell.class,
                BucketPhotoUploadCell.class,
                BucketPhotoCell.class,
                BucketPhotoCellForeign.class,
                BucketPhotoCellForDetails.class,
                FixedPhotoFsPresenter.class,
                BucketPhotosView.class,
                ForeignBucketItemDetailsPresenter.class,
                ForeignBucketDetailsFragment.class,
                ForeignBucketTabsFragment.class,
                ForeignBucketTabPresenter.class,
                ForeignBucketListFragment.class,
                BucketItemStaticCell.class,
                ForeignBucketListPresenter.class
        },
        complete = false,
        library = true
)
public class BucketListModule {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";
    public static final String EXTRA_ITEM = "EXTRA_ITEM";
        public static final String EXTRA_LOCK = "EXTRA_LOCK";
    public static final String BUCKETLIST = Route.BUCKET_LIST.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideBucketListComponent() {
        return new ComponentDescription(BUCKETLIST, R.string.bucket_list, R.string.bucket_list, R.drawable.ic_bucket_lists, BucketTabsFragment.class);
    }
}

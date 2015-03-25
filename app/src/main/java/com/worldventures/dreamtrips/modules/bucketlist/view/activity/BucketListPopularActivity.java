package com.worldventures.dreamtrips.modules.bucketlist.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPopularActivityPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;

import butterknife.InjectView;

/**
 *  1 on 26.02.15.
 */
@Layout(R.layout.activity_book_it)
public class BucketListPopularActivity extends ActivityWithPresenter<BucketListPopularActivityPresenter> {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String EXTRA_STATE = "EXTRA_STATE";

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);

        BucketTabsFragment.Type type = (BucketTabsFragment.Type) bundleExtra.getSerializable(EXTRA_TYPE);
        Route route = (Route) bundleExtra.getSerializable(EXTRA_STATE);

        getPresentationModel().onCreate(bundleExtra, route);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (route.equals(Route.POPULAR_BUCKET))
            switch (type) {
                case LOCATIONS:
                    getSupportActionBar().setTitle(R.string.bucket_list_location_popular);
                    break;
                case ACTIVITIES:
                    getSupportActionBar().setTitle(R.string.bucket_list_activity_popular);
                    break;
                case RESTAURANTS:
                    getSupportActionBar().setTitle(R.string.bucket_list_dinning_popular);
                    break;
            }

        toolbar.setBackgroundColor(getResources().getColor(R.color.theme_main));
    }

    @Override
    protected BucketListPopularActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new BucketListPopularActivityPresenter(this);
    }
}

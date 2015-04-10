package com.worldventures.dreamtrips.modules.bucketlist.view.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.activity_book_it)
public class BucketActivity extends ActivityWithPresenter<ActivityPresenter> {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String EXTRA_STATE = "EXTRA_STATE";

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @Inject
    protected FragmentCompass fragmentCompass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);

        BucketTabsFragment.Type type = (BucketTabsFragment.Type) bundleExtra.getSerializable(EXTRA_TYPE);
        Route route = (Route) bundleExtra.getSerializable(EXTRA_STATE);

        fragmentCompass.switchBranch(route, bundleExtra);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.getMenu().clear();

        if (route.equals(Route.POPULAR_TAB_BUCKER)) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.theme_main));
            switch (type) {
                case ACTIVITIES:
                    getSupportActionBar().setTitle(R.string.bucket_list_activity_popular);
                    break;
                case RESTAURANTS:
                    getSupportActionBar().setTitle(R.string.bucket_list_dinning_popular);
                    break;
                default:
                    getSupportActionBar().setTitle(R.string.bucket_list_location_popular);
                    break;
            }
        } else if (route.equals(Route.BUCKET_EDIT)) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.theme_main));
            getSupportActionBar().setTitle(R.string.bucket_list_edit_header);
        } else if (route.equals(Route.DETAIL_BUCKET)) {
            toolbar.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

    }

    @Override
    protected ActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new ActivityPresenter(this);
    }
}
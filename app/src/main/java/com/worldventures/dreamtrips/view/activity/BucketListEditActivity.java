package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.presentation.BucketListEditActivityPM;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import butterknife.InjectView;

/**
 * Created by 1 on 26.02.15.
 */
@Layout(R.layout.activity_book_it)
public class BucketListEditActivity extends PresentationModelDrivenActivity<BucketListEditActivityPM> {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_STATE = "EXTRA_STATE";

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);

        BucketTabsFragment.Type type = (BucketTabsFragment.Type) bundleExtra.getSerializable(EXTRA_TYPE);
        State state = (State) bundleExtra.getSerializable(EXTRA_STATE);

        getPresentationModel().onCreate(bundleExtra, state);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (state.equals(State.POPULAR_BUCKET))
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
        else
            getSupportActionBar().setTitle(R.string.bucket_list_my_title);

        toolbar.setBackgroundColor(getResources().getColor(R.color.theme_main));
    }

    public boolean isTabletLandscape() {
        return ViewUtils.isTablet(this) && ViewUtils.isLandscapeOrientation(this);
    }

    @Override
    protected BucketListEditActivityPM createPresentationModel(Bundle savedInstanceState) {
        return new BucketListEditActivityPM(this);
    }
}

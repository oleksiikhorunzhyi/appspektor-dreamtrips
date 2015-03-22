package com.worldventures.dreamtrips.modules.trips.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DetailTripActivityPresenter;

@Layout(R.layout.activity_detail_trip)
public class DetailTripActivity extends ActivityWithPresenter<DetailTripActivityPresenter> {
    public static final String EXTRA_TRIP = "EXTRA_TRIP";

    @Override
    protected DetailTripActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new DetailTripActivityPresenter(this);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        this.getPresentationModel().onCreate(getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE), savedInstanceState);
    }
}

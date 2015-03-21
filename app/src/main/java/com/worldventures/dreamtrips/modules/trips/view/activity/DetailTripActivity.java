package com.worldventures.dreamtrips.modules.trips.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.trips.presenter.DetailTripActivityPM;
import com.worldventures.dreamtrips.modules.common.view.activity.PresentationModelDrivenActivity;

/**
 * Created by Edward on 19.01.15.
 * activity for detailed photo
 */
@Layout(R.layout.activity_detail_trip)
public class DetailTripActivity extends PresentationModelDrivenActivity<DetailTripActivityPM> {
    public static final String EXTRA_TRIP = "EXTRA_TRIP";
    public static final String EXTRA_TRIPS = "EXTRA_TRIPS";

    @Override
    protected DetailTripActivityPM createPresentationModel(Bundle savedInstanceState) {
        return new DetailTripActivityPM(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        this.getPresentationModel().onCreate(getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE), savedInstanceState);
    }
}
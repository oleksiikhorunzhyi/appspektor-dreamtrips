package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;


/**
 * Created by Edward on 19.01.15.
 * presentation model for activity with detailed trip
 */
public class DetailTripActivityPM extends BasePresenter<BasePresenter.View> {

    public DetailTripActivityPM(BasePresenter.View view) {
        super(view);
    }

    public void onCreate(Bundle bundle, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fragmentCompass.add(Route.DETAILED_TRIP, bundle);
        }
    }

}

package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class DetailTripActivityPresenter extends Presenter<Presenter.View> {

    public DetailTripActivityPresenter(Presenter.View view) {
        super();
    }

    public void onCreate(Bundle bundle, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fragmentCompass.add(Route.DETAILED_TRIP, bundle);
        }
    }

}

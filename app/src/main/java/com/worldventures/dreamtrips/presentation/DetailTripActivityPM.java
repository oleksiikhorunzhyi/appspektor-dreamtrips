package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.State;


/**
 * Created by Edward on 19.01.15.
 * presentation model for activity with detailed trip
 */
public class DetailTripActivityPM extends BasePresentation<BasePresentation.View> {

    public DetailTripActivityPM(BasePresentation.View view) {
        super(view);
    }

    public void onCreate(Bundle bundle, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fragmentCompass.add(State.DETAILED_TRIP, bundle);
        }
    }

}

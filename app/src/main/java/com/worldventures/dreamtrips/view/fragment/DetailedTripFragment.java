package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.DetailedTripFragmentPM;

/**
 * Created by Edward on 19.01.15.
 * fragment to show detailed trip
 */
@Layout(R.layout.fragment_detailed_trip)
public class DetailedTripFragment extends BaseFragment<DetailedTripFragmentPM> implements DetailedTripFragmentPM.View {

    @Override
    protected DetailedTripFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new DetailedTripFragmentPM(this);
    }
}

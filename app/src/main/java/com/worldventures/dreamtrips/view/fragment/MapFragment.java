package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;

import com.worldventures.dreamtrips.presentation.DreamTripsFragmentPM;

/**
 * Created by 1 on 26.01.15.
 */
public class MapFragment extends BaseFragment<DreamTripsFragmentPM> implements DreamTripsFragmentPM.View {

    @Override
    protected DreamTripsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new DreamTripsFragmentPM(this);
    }

    @Override
    public void dataSetChanged() {

    }

    @Override
    public void showErrorMessage() {

    }
}

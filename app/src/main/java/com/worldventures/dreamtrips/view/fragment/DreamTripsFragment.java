package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.presentation.DummyPresentationModel;

import org.robobinding.annotation.PresentationModel;

@Layout(R.layout.fragment_dream_trips)
public class DreamTripsFragment extends BaseFragment<DummyPresentationModel> {


    public DreamTripsFragment() {

    }

    @Override
    protected DummyPresentationModel createPresentationModel(Bundle savedInstanceState) {
        return new DummyPresentationModel(this);
    }
}

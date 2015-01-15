package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.BasePresentation;

@Layout(R.layout.fragment_dream_trips)
public class DreamTripsFragment extends BaseFragment<DreamTripsFragment.DummyPresentationModel> {

    public static class DummyPresentationModel extends BasePresentation<BasePresentation.View> {

        public DummyPresentationModel(View view) {
            super(view);
        }
    }

    public DreamTripsFragment() {

    }

    @Override
    protected DummyPresentationModel createPresentationModel(Bundle savedInstanceState) {
        return new DummyPresentationModel(this);
    }
}

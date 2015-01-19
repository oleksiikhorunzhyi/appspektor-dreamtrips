package com.worldventures.dreamtrips.presentation;

import org.robobinding.annotation.PresentationModel;

/**
 * Created by Edward on 19.01.15.
 * presentation model for DetailedTripFragment
 */
@PresentationModel
public class DetailedTripFragmentPM extends BasePresentation<DetailedTripFragmentPM.View> {

    public DetailedTripFragmentPM(View view) {
        super(view);
    }

    public static interface View extends BasePresentation.View {
    }
}

package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.worldventures.dreamtrips.R;

public class OfferNoReviewFragment extends Fragment {

    public static OfferNoReviewFragment newInstance(Bundle arguments) {
        OfferNoReviewFragment f = new OfferNoReviewFragment();
        if (arguments != null) {
            f.setArguments(arguments);
        }
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
        return inflater.inflate(R.layout.offer_details_no_review, container, false);
    }

}

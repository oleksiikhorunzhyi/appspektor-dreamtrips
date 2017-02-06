package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.util.ArrayList;

public class OfferNoReviewFragment extends Fragment {

    public OfferNoReviewFragment() {
    }

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

package com.worldventures.dreamtrips.view.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class DreamTripsFragment extends BaseFragment<MainActivity> {


    public DreamTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dream_trips, container, false);
    }


}

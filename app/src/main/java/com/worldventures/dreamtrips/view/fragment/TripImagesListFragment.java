package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.presentation.TripImagesListFragmentPresentation;

import org.robobinding.ViewBinder;

import butterknife.ButterKnife;

public class TripImagesListFragment extends BaseFragment<MainActivity> implements TripImagesListFragmentPresentation.View {

    private TripImagesListFragmentPresentation presentationModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        presentationModel = new TripImagesListFragmentPresentation(this, getAbsActivity());
        ViewBinder viewBinder = getAbsActivity().createViewBinder();
        View view = viewBinder.inflateAndBindWithoutAttachingToRoot(R.layout.fragment_trip_list_images, presentationModel, container);
        ButterKnife.inject(this, view);

        return view;
    }


}

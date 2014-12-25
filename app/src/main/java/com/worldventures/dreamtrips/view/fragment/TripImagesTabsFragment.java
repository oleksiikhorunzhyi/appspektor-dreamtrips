package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.adapter.BasePagerAdapter;
import com.worldventures.dreamtrips.view.presentation.TripImagesTabsFragmentPresentation;

import org.robobinding.ViewBinder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TripImagesTabsFragment extends BaseFragment<MainActivity> implements TripImagesTabsFragmentPresentation.View {


    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    TripImagesTabsFragmentPresentation presentationModel;
    private BasePagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        presentationModel = new TripImagesTabsFragmentPresentation(this, getAbsActivity());
        ViewBinder viewBinder = getAbsActivity().createViewBinder();
        View view = viewBinder.inflateAndBindWithoutAttachingToRoot(R.layout.fragment_trip_tabs_images, presentationModel, container);
        ButterKnife.inject(this, view);

        adapter = new BasePagerAdapter(getChildFragmentManager());
        adapter.add(new BasePagerAdapter.FragmentItem(TripImagesListFragment.class, "Member images"));
        adapter.add(new BasePagerAdapter.FragmentItem(TripImagesListFragment.class, "My images"));
        adapter.add(new BasePagerAdapter.FragmentItem(TripImagesListFragment.class, "You should be here"));
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        return view;
    }

}

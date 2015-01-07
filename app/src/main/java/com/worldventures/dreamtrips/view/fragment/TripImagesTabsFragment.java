package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.adapter.BasePagerAdapter;
import com.worldventures.dreamtrips.view.presentation.TripImagesTabsFragmentPresentation;

import org.robobinding.ViewBinder;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.BUNDLE_TYPE;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public class TripImagesTabsFragment extends BaseFragment<MainActivity> implements TripImagesTabsFragmentPresentation.View, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {


    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.multiple_actions_down)
    FloatingActionsMenu actionsMenu;
    @InjectView(R.id.v_bg_holder)
    View bgHolder;

    TripImagesTabsFragmentPresentation presentationModel;
    BasePagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        presentationModel = new TripImagesTabsFragmentPresentation(this, getAbsActivity());
        ViewBinder viewBinder = getAbsActivity().createViewBinder();
        View view = viewBinder.inflateAndBindWithoutAttachingToRoot(R.layout.fragment_trip_tabs_images, presentationModel, container);
        ButterKnife.inject(this, view);

        adapter = new BasePagerAdapter(getChildFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                Bundle args = new Bundle();
                Type type = position == 0 ? Type.MEMBER_IMAGES : position == 1 ? Type.MY_IMAGES : Type.YOU_SHOULD_BE_HERE;
                args.putSerializable(BUNDLE_TYPE, type);
                fragment.setArguments(args);
            }
        };
        adapter.add(new BasePagerAdapter.FragmentItem(TripImagesListFragment.class, getString(R.string.member_images)));
        adapter.add(new BasePagerAdapter.FragmentItem(TripImagesListFragment.class, getString(R.string.my_images)));
        adapter.add(new BasePagerAdapter.FragmentItem(TripImagesListFragment.class, getString(R.string.you_should_be_here)));
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        actionsMenu.setOnFloatingActionsMenuUpdateListener(this);
        return view;
    }

    @Override
    public void onMenuExpanded() {
        bgHolder.setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
    }

    @Override
    public void onMenuCollapsed() {
        bgHolder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}

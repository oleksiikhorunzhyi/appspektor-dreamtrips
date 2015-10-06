package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.DataFragmentItem;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesTabsPresenter;

import java.util.List;

import butterknife.InjectView;
import icepick.State;

@Layout(R.layout.fragment_dtl_places_tabs)
public class DtlPlacesTabsFragment extends BaseFragment<DtlPlacesTabsPresenter> implements DtlPlacesTabsPresenter.View {

    @InjectView(R.id.tabs)
    TabLayout tabStrip;
    @InjectView(R.id.pager)
    CustomViewPager pager;
    BasePagerAdapter<DataFragmentItem> adapter;

    @State
    int currentPosition;

    @Override
    protected DtlPlacesTabsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPlacesTabsPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (adapter == null) {
            adapter = new BasePagerAdapter<DataFragmentItem>(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    super.setArgs(position, fragment);
                    // TODO : implement when passing parameters starts make sense
//                    Bundle args = createListFragmentArgs(position);
//                    fragment.setArguments(args);
                }
            };
        }
        pager.setAdapter(adapter);
        pager.setPagingEnabled(false);
        pager.setOffscreenPageLimit(1);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                notifyPosition();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyPosition();
    }

    private void notifyPosition() {
        getPresenter().onTabChange(DtlPlaceType.values()[currentPosition]);
    }

    @Override
    public void setTypes(List<DtlPlaceType> types) {
        if (adapter.getCount() == 0) {
            for (DtlPlaceType type : types) {
                adapter.add(new DataFragmentItem<>(DtlPlacesListFragment.class, getString(type.getRes()), type));
            }
            adapter.notifyDataSetChanged();
        }
        tabStrip.setupWithViewPager(pager);
    }

    @Override
    public void updateSelection() {
        pager.setCurrentItem(currentPosition);
    }
}

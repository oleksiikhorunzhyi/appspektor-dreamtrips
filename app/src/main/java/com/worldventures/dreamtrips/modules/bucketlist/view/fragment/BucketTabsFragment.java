package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icicle;

import static com.astuetz.PagerSlidingTabStrip.CustomTabProvider;
import static com.innahema.collections.query.queriables.Queryable.from;
import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType;


@Layout(R.layout.fragment_bucket_tab)
@MenuResource(R.menu.menu_mock)
public class BucketTabsFragment extends BaseFragment<BucketTabsPresenter> implements BucketTabsPresenter.View {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabStrip;
    @InjectView(R.id.pager)
    CustomViewPager pager;
    BucketTabsAdapter adapter;

    @Icicle
    int currentPosition;

    @Override
    protected BucketTabsPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketTabsPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (adapter == null) {
            adapter = new BucketTabsAdapter(getChildFragmentManager(), tabStrip);
        }

        pager.setAdapter(adapter);
        pager.setPagingEnabled(false);
        pager.setOffscreenPageLimit(2);
        tabStrip.setViewPager(pager);
        tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                BucketType bucketType = adapter.getFragmentItem(position).data;
                getPresenter().onTabChange(bucketType);
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void updateSelection() {
        pager.setCurrentItem(currentPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.getCount() > 0) {
            BucketType currentType = adapter.getFragmentItem(pager.getCurrentItem()).data;
            getPresenter().onTabChange(currentType);
        }
    }

   @Override
    public void setTypes(List<BucketType> types) {
        if (adapter.getCount() > 0) return;
        //
        for (BucketType type : types) {
            adapter.add(new DataFragmentItem<>(BucketListFragment.class, getString(type.getRes()), type));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setRecentBucketItemsCount(Map<BucketType, Integer> items) {
        for (BucketType type : items.keySet()) {
            adapter.setBadgeCount(type, items.get(type));
        }
    }

    @Override
    public void resetRecentlyAddedBucketItem(BucketType type) {
        adapter.setBadgeCount(type, 0);
    }

    public static class DataFragmentItem<T extends Serializable> extends FragmentItem {
        public final T data;

        public DataFragmentItem(Class<? extends Fragment> aClass, String title, T data) {
            super(aClass, title);
            this.data = data;
        }
    }

    public static class BucketTabsAdapter extends BasePagerAdapter<DataFragmentItem<BucketType>> implements CustomTabProvider {
        ViewGroup tabHolder;
        WeakHandler handler = new WeakHandler();

        public BucketTabsAdapter(FragmentManager fm, ViewGroup tabHolder) {
            super(fm);
            this.tabHolder = tabHolder;
        }

        @Override
        public void setArgs(int position, Fragment fragment) {
            Bundle args = new Bundle();
            BucketType type = getFragmentItem(position).data;
            args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
            fragment.setArguments(args);
        }

        @Override
        public View getCustomTabView(ViewGroup viewGroup, int i) {
            View viewWithBadge = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_tab_with_badge, viewGroup, false);
            ButterKnife.findById(viewWithBadge, R.id.psts_tab_title).setOnClickListener((v) -> viewWithBadge.performClick());
            ButterKnife.findById(viewWithBadge, R.id.psts_tab_badge).setAlpha(0f);
            return viewWithBadge;
        }

        public void setBadgeCount(BucketType type, int count) {
            int pos = fragmentItems.indexOf(from(fragmentItems).firstOrDefault(f -> f.data.equals(type)));
            if (pos == -1) return;
            //
            View tab = ((ViewGroup) tabHolder.getChildAt(0)).getChildAt(pos);
            TextView badge = ButterKnife.<TextView>findById(tab, R.id.psts_tab_badge);
            float alpha;
            long duration, delay;
            if (count == 0) {
                alpha = 0f;
                duration = 500l;
                delay = duration;
            } else {
                alpha = 1f;
                duration = 300l;
                delay = 0;
            }
            handler.postDelayed(() -> badge.setText(String.valueOf(count)), delay);
            badge.animate().alpha(alpha).setDuration(duration);
        }
    }
}

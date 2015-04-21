package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType;


@Layout(R.layout.fragment_bucket_tab)
public class BucketTabsFragment extends BaseFragment<BucketTabsPresenter> implements BucketTabsPresenter.View {

    @InjectView(R.id.tabs) PagerSlidingTabStrip tabStrip;
    @InjectView(R.id.pager) CustomViewPager pager;

    BasePagerAdapter<DataFragmentItem<BucketType>> adapter;

    @Override
    protected BucketTabsPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketTabsPresenter(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (adapter == null) {
            adapter = new BucketTabsAdapter(getChildFragmentManager());
        }

        pager.setAdapter(adapter);
        pager.setPagingEnabled(false);
        tabStrip.setViewPager(pager);
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

    }

    public static class DataFragmentItem<T extends Serializable> extends FragmentItem {
        public final T data;

        public DataFragmentItem(Class<? extends Fragment> aClass, String title, T data) {
            super(aClass, title);
            this.data = data;
        }
    }

    public static class BucketTabsAdapter extends BasePagerAdapter<DataFragmentItem<BucketType>> {
        public BucketTabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setArgs(int position, Fragment fragment) {
            Bundle args = new Bundle();
            BucketType type = getFragmentItem(position).data;
            args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
            fragment.setArguments(args);
        }
    }
}

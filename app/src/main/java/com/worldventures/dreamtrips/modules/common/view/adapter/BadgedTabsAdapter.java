package com.worldventures.dreamtrips.modules.common.view.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.DataFragmentItem;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;

import java.io.Serializable;

import butterknife.ButterKnife;

public class BadgedTabsAdapter<T extends Serializable> extends BasePagerAdapter<DataFragmentItem<T>>
        implements PagerSlidingTabStrip.CustomTabProvider {
        ViewGroup tabHolder;
        WeakHandler handler = new WeakHandler();

        public BadgedTabsAdapter(FragmentManager fm, ViewGroup tabHolder) {
            super(fm);
            this.tabHolder = tabHolder;
        }

        @Override
        public void setArgs(int position, Fragment fragment) {
            Bundle args = new Bundle();
            T type = getFragmentItem(position).data;
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

        public void setBadgeCount(T type, int count) {
            int pos = fragmentItems.indexOf(Queryable.from(fragmentItems).firstOrDefault(f -> f.data.equals(type)));
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

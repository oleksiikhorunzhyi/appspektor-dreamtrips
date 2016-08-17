package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;

public class BadgedTabLayout extends TabLayout {
   WeakHandler handler = new WeakHandler();

   public BadgedTabLayout(Context context) {
      super(context);
   }

   public BadgedTabLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public BadgedTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public void setupWithPagerBadged(ViewPager viewPager) {
      PagerAdapter adapter = viewPager.getAdapter();
      if (adapter == null) {
         throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
      } else {
         this.setTabsFromPagerAdapterBadged(adapter);
         viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this));
         this.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
      }
   }

   public void setTabsFromPagerAdapterBadged(PagerAdapter adapter) {
      this.removeAllTabs();
      int i = 0;

      for (int count = adapter.getCount(); i < count; ++i) {
         View tabView = getCustomTabView(this, adapter.getPageTitle(i), i);
         this.addTab(this.newTab().setTag(tabView).setCustomView(tabView));
      }
   }

   public View getCustomTabView(ViewGroup viewGroup, CharSequence title, int index) {
      View viewWithBadge = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.view_tab_with_badge, viewGroup, false);
      ButterKnife.findById(viewWithBadge, R.id.psts_tab_title).setOnClickListener((v) -> viewWithBadge.performClick());
      ButterKnife.<TextView>findById(viewWithBadge, R.id.psts_tab_title).setText(title);
      ButterKnife.findById(viewWithBadge, R.id.psts_tab_badge).setAlpha(0f);
      viewWithBadge.setOnClickListener((view -> {
         getTabAt(index).select();
      }));

      return viewWithBadge;
   }

   public void setBadgeCount(int pos, int count) {
      if (getTabCount() > 0) {
         View tab = (ViewGroup) getTabAt(pos).getTag();
         BadgeView badge = ButterKnife.findById(tab, R.id.psts_tab_badge);
         badge.setBadgeBackgroundColor(getResources().getColor(R.color.bucket_red));

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

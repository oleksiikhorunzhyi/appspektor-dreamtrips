package com.techery.spares.utils;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import com.techery.spares.adapter.FragmentTabsPagerAdapter;
import com.techery.spares.ui.activity.InjectingActivity;

import java.util.List;

public class TabsController implements ActionBar.TabListener {
   private ViewPager viewPager;
   private final InjectingActivity activity;

   public TabsController(InjectingActivity activity) {
      this.activity = activity;
   }

   public void setup(ViewPager viewPager, List<FragmentTabsPagerAdapter.TabItem> tabItems) {
      this.viewPager = viewPager;
      activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
      setupTabs(this.viewPager, tabItems);
   }

   protected void setupTabs(ViewPager viewPager, List<FragmentTabsPagerAdapter.TabItem> tabItems) {
      final ActionBar actionBar = activity.getSupportActionBar();
      FragmentTabsPagerAdapter sectionsPagerAdapter = new FragmentTabsPagerAdapter(activity.getSupportFragmentManager(), tabItems);

      viewPager.setAdapter(sectionsPagerAdapter);
      viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
         @Override
         public void onPageSelected(int position) {
            actionBar.setSelectedNavigationItem(position);
         }
      });

      viewPager.getAdapter().notifyDataSetChanged();

      actionBar.removeAllTabs();

      for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
         actionBar.addTab(actionBar.newTab().setText(sectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
      }
   }

   @Override
   public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
      viewPager.setCurrentItem(tab.getPosition());
   }

   @Override
   public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
      //nothing to do here
   }

   @Override
   public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
      //nothing to do here
   }
}

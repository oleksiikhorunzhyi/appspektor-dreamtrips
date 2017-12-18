package com.worldventures.wallet.ui.settings.general.display.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

public class DisplayOptionsPagerAdapter extends PagerAdapter {

   public static final List<Integer> DISPLAY_OPTIONS = new ArrayList<Integer>() {
      { //NOPMD
         add(SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY);
         add(SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME);
         add(SetHomeDisplayTypeAction.DISPLAY_NAME_ONLY);
         add(SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME);
      }
   };

   private static final List<Integer> DISPLAY_OPTION_TITLES = new ArrayList<Integer>() {
      { //NOPMD
         add(R.string.wallet_settings_general_display_photo_only);
         add(R.string.wallet_settings_general_display_photo_first_name);
         add(R.string.wallet_settings_general_display_full_name_only);
         add(R.string.wallet_settings_general_display_full_name_phone);
      }
   };

   private final LayoutInflater inflater;
   private final ProfileViewModel profileViewModel;
   private final DisplayOptionsClickListener clickListener;

   public DisplayOptionsPagerAdapter(@NonNull Context context, @NonNull ProfileViewModel profileViewModel, DisplayOptionsClickListener clickListener) {
      this.inflater = LayoutInflater.from(context);
      this.profileViewModel = profileViewModel;
      this.clickListener = clickListener;
   }

   @Override
   public Object instantiateItem(ViewGroup collection, int position) {
      final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_wallet_settings_display, collection, false);

      final DisplayOptionsViewHolder holder = new DisplayOptionsViewHolder(layout);
      holder.bindData(DISPLAY_OPTIONS.get(position), DISPLAY_OPTION_TITLES.get(position), profileViewModel);
      holder.setClickListener(clickListener);

      layout.setTag(holder);
      collection.addView(layout);
      registerDataSetObserver(holder.dataSetObserver);
      return layout;
   }


   @Override
   public void destroyItem(ViewGroup collection, int position, Object view) {
      final View currentView = (View) view;
      unregisterDataSetObserver(((DisplayOptionsViewHolder) currentView.getTag()).dataSetObserver);
      collection.removeView(currentView);
   }

   @Override
   public int getCount() {
      return DISPLAY_OPTIONS.size();
   }

   @Override
   public boolean isViewFromObject(View view, Object object) {
      return view == object;
   }

}

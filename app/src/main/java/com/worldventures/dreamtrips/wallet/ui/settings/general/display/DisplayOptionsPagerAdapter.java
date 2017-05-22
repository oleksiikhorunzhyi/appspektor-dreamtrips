package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

class DisplayOptionsPagerAdapter extends PagerAdapter {

   static final List<Integer> DISPLAY_OPTIONS = new ArrayList<Integer>() {
      {
         add(SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY);
         add(SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME);
         add(SetHomeDisplayTypeAction.DISPLAY_NAME_ONLY);
         add(SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME);

      }
   };

   private static final List<Integer> DISPLAY_OPTION_TITLES = new ArrayList<Integer>() {
      {
         add(R.string.wallet_settings_general_display_photo_only);
         add(R.string.wallet_settings_general_display_photo_first_name);
         add(R.string.wallet_settings_general_display_full_name_only);
         add(R.string.wallet_settings_general_display_full_name_phone);
      }
   };

   private final LayoutInflater inflater;
   private final SmartCardUser smartCardUser;
   private final DisplayOptionsClickListener clickListener;

   DisplayOptionsPagerAdapter(@NonNull Context context, @NonNull SmartCardUser smartCardUser, DisplayOptionsClickListener clickListener) {
      inflater = LayoutInflater.from(context);
      this.smartCardUser = smartCardUser;
      this.clickListener = clickListener;
   }

   @Override
   public Object instantiateItem(ViewGroup collection, int position) {
      ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.adapter_item_wallet_settings_display, collection, false);

      DisplayOptionsViewHolder holder = new DisplayOptionsViewHolder(layout);
      holder.bindData(DISPLAY_OPTIONS.get(position), DISPLAY_OPTION_TITLES.get(position), smartCardUser);
      holder.setClickListener(clickListener);

      layout.setTag(holder);
      collection.addView(layout);

      return layout;
   }


   @Override
   public void destroyItem(ViewGroup collection, int position, Object view) {
      collection.removeView((View) view);
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
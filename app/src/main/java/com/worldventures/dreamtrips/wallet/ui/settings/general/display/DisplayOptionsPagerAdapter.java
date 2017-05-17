package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

class DisplayOptionsPagerAdapter extends PagerAdapter {

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
      holder.bindData(smartCardUser, DisplayOptionsEnum.values()[position]);
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
      return DisplayOptionsEnum.values().length;
   }

   @Override
   public boolean isViewFromObject(View view, Object object) {
      return view == object;
   }

}
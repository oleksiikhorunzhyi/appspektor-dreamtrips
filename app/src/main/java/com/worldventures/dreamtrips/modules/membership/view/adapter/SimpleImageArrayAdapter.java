package com.worldventures.dreamtrips.modules.membership.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.worldventures.dreamtrips.R;

public class SimpleImageArrayAdapter extends ArrayAdapter<Integer> {
   private Integer[] images;

   public SimpleImageArrayAdapter(Context context, Integer[] images) {
      super(context, android.R.layout.simple_spinner_item, images);
      this.images = images;
   }

   @Override
   public View getDropDownView(int position, View convertView, ViewGroup parent) {
      ImageView imageView = (ImageView) LayoutInflater.from(getContext())
            .inflate(R.layout.adapter_item_popup_image, null);
      imageView.setImageResource(images[position]);
      imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      return imageView;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      return getImageForPosition(position);
   }

   private View getImageForPosition(int position) {
      ImageView imageView = new ImageView(getContext());
      imageView.setBackgroundResource(images[position]);
      imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      return imageView;
   }
}
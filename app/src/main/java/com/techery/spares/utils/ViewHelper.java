package com.techery.spares.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class ViewHelper {

   private ViewHelper() {
   }

   public static void inflateResource(int resource, ViewGroup viewGroup) {
      LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      inflater.inflate(resource, viewGroup, true);
      ButterKnife.inject(viewGroup);
   }
}

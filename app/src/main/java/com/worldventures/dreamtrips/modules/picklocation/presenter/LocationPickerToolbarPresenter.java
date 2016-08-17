package com.worldventures.dreamtrips.modules.picklocation.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.worldventures.dreamtrips.R;

public class LocationPickerToolbarPresenter {

   private Toolbar toolbar;
   private Context context;

   public LocationPickerToolbarPresenter(Toolbar toolbar, Context context) {
      this.context = context;
      this.toolbar = toolbar;
      initToolbar();
   }

   private void initToolbar() {
      toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_main));
      toolbar.setTitleTextAppearance(context, R.style.ActionBarTitle);
      toolbar.setSubtitleTextAppearance(context, R.style.ActionBarSubtitle);
   }

   public void setTitle(@StringRes int name) {
      toolbar.setTitle(name);
   }

   public void setTitle(String name) {
      toolbar.setTitle(name);
   }

   public void setSubtitle(@StringRes int name) {
      toolbar.setSubtitle(name);
   }

   public void setSubtitle(String name) {
      toolbar.setSubtitle(name);
   }

   public void enableUpNavigationButton() {
      TypedValue typedValue = new TypedValue();
      context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.homeAsUpIndicator, typedValue, true);
      toolbar.setNavigationIcon(typedValue.resourceId);
      toolbar.setNavigationOnClickListener(v -> ((Activity) context).onBackPressed());
   }
}


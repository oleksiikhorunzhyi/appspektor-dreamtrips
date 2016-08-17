package com.messenger.ui.adapter.inflater;

import android.content.Context;
import android.view.View;

import butterknife.ButterKnife;

public abstract class ViewInflater {

   protected Context context;

   protected View rootView;

   public void setView(View rootView) {
      this.rootView = rootView;
      ButterKnife.inject(this, rootView);
      context = rootView.getContext();
   }
}

package com.techery.spares.adapter;

import android.content.Context;

import com.techery.spares.module.Injector;

public class LoaderRecycleAdapter<BaseItemClass> extends BaseDelegateAdapter<BaseItemClass> {

   public LoaderRecycleAdapter(Context context, Injector injector) {
      super(context, injector);
   }

   public void onStartLoading() {
      //nothing to do here
   }



   public void onError(Throwable throwable) {
      //nothing to do here
   }
}

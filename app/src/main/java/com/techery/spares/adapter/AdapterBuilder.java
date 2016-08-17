package com.techery.spares.adapter;

import android.content.Context;

import com.techery.spares.module.Injector;

public class AdapterBuilder {

   private final Injector injector;
   private final Context context;

   public AdapterBuilder(Injector injector, Context context) {
      this.injector = injector;
      this.context = context;
   }
}

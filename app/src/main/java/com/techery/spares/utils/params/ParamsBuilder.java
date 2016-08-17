package com.techery.spares.utils.params;

import android.content.Intent;

import com.google.gson.Gson;

public class ParamsBuilder {
   private final Gson gson = new Gson();
   private final Intent intent;

   ParamsBuilder(Intent intent) {
      this.intent = (Intent) intent.clone();
   }

   public void add(String name, Object param) {
      this.intent.putExtra(name, gson.toJson(param));
   }

   public Intent build() {
      return this.intent;
   }
}

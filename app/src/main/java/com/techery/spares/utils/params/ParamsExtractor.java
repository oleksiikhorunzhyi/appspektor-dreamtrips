package com.techery.spares.utils.params;

import android.os.Bundle;

import com.google.gson.Gson;
import com.techery.spares.ui.activity.InjectingActivity;

public class ParamsExtractor {
   private final Gson gson = new Gson();
   private final InjectingActivity activity;

   public ParamsExtractor(InjectingActivity activity) {
      this.activity = activity;
   }

   public <T> T get(String name, Class<T> paramClass) {
      Bundle extras = this.activity.getIntent().getExtras();
      String jsonValue = extras.getString(name);
      if (jsonValue != null) {
         return gson.fromJson(jsonValue, paramClass);
      } else {
         return (T) extras.getParcelable(name);
      }
   }
}

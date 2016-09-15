package com.techery.spares.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.techery.spares.utils.ValidationUtils;


public class ServiceActionRunner {
   private final Context context;

   public ServiceActionRunner(Context context) {
      ValidationUtils.checkNotNull(context, "Context is required");
      this.context = context;
   }


   public <T extends Service> Runner from(Class<T> clazz) {
      return new Runner<>(clazz);
   }

   public class Runner<T extends Service> {
      private final Class<T> clazz;

      public Runner(Class<T> clazz) {
         ValidationUtils.checkNotNull(clazz, "Class is required");
         this.clazz = clazz;
      }

      public void run(Object action) {
         ValidationUtils.checkNotNull(action, "Action is required to run service action");

         Intent intent = new Intent(context, this.clazz);

         intent.setAction(action.getClass().getName());

         Gson gson = new Gson();
         String jsonPayload = gson.toJson(action);

         intent.putExtra(InjectingService.EXTRA_PAYLOAD, jsonPayload);

         context.startService(intent);
      }

      public void start() {
         Intent intent = new Intent(context, this.clazz);
         context.startService(intent);
      }
   }
}

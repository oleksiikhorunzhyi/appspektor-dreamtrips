package com.techery.spares.service;

import android.content.Intent;

import com.google.gson.Gson;
import com.techery.spares.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class ServiceActionRouter {

   private Map<String, ActionHandler> actionsMap = new HashMap<>();
   private Gson gson = new Gson();

   public interface ActionHandler {
      public void run(Intent intent);
   }

   public interface ActionHandlerWithPayload<T> {
      public void run(T payload);
   }

   public void dispatchIntent(Intent intent) {
      if (intent == null) {
         return;
      }

      final String action = intent.getAction();

      if (action != null) {
         ActionHandler body = actionsMap.get(action);

         if (body != null) {
            body.run(intent);
         } else {
            throw new IllegalArgumentException("Unknown action:" + action + " for service:" + intent.getComponent()
                  .getClassName());
         }
      }
   }

   public void on(String action, ActionHandler body) {
      ValidationUtils.checkNotNull(action);
      ValidationUtils.checkNotNull(body);

      actionsMap.put(action, body);
   }

   public <T> void on(String action, Class<T> clazz, ActionHandlerWithPayload<T> body) {
      ValidationUtils.checkNotNull(action);
      ValidationUtils.checkNotNull(body);

      actionsMap.put(action, (intent) -> {
         String jsonPayload = intent.getStringExtra(InjectingService.EXTRA_PAYLOAD);

         if (jsonPayload != null) {
            T payload = this.gson.fromJson(jsonPayload, clazz);

            body.run(payload);
         } else {
            body.run(null);
         }
      });
   }

   public <T> void on(Class<T> clazz, ActionHandlerWithPayload<T> body) {
      on(clazz.getName(), clazz, body);
   }
}

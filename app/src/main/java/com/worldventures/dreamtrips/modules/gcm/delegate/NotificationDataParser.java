package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.os.Bundle;

import com.google.gson.Gson;
import com.worldventures.dreamtrips.modules.gcm.model.PushMessage;

public class NotificationDataParser {

   private final Gson gson;

   public NotificationDataParser(Gson gson) {
      this.gson = gson;
   }

   private String getJson(Bundle data) {
      return data.getString("json");
   }

   public <U extends PushMessage> U parseMessage(Bundle data, Class<U> parseClazz) {
      return gson.fromJson(getJson(data), parseClazz);
   }

}

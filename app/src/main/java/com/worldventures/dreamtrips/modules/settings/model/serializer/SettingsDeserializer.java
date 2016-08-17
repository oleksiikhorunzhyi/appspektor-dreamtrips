package com.worldventures.dreamtrips.modules.settings.model.serializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.modules.settings.model.FlagSetting;
import com.worldventures.dreamtrips.modules.settings.model.SelectSetting;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SettingsDeserializer<T extends Setting> implements JsonDeserializer<T> {

   private Gson gson;
   private Map<String, Class<? extends Setting>> modelByName = new HashMap<>();

   {
      modelByName.put(SettingsFactory.DISTANCE_UNITS, SelectSetting.class);
      modelByName.put(SettingsFactory.FRIEND_REQUEST, FlagSetting.class);
      modelByName.put(SettingsFactory.NEW_MESSAGE, FlagSetting.class);
      modelByName.put(SettingsFactory.PHOTO_TAGGING, FlagSetting.class);
   }

   public SettingsDeserializer() {
      gson = new GsonBuilder().serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
   }

   @Override
   public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      String name = null;
      JsonElement nameElement = json.getAsJsonObject().get("name");
      if (!nameElement.isJsonNull()) {
         name = gson.fromJson(nameElement.getAsJsonPrimitive(), String.class);
      }
      Setting model;
      if (name == null || !modelByName.containsKey(name)) model = new Setting<>("", Setting.Type.UNKNOWN, "");
      else model = gson.fromJson(json, modelByName.get(name));

      return (T) model;
   }
}

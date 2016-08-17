package com.worldventures.dreamtrips.modules.settings.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.worldventures.dreamtrips.modules.settings.model.FlagSetting;
import com.worldventures.dreamtrips.modules.settings.model.SelectSetting;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import java.lang.reflect.Type;

public class SettingsSerializer implements JsonSerializer<Setting> {

   @Override
   public JsonElement serialize(Setting src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("name", src.getName());
      switch (src.getType()) {
         case FLAG:
            jsonObject.addProperty("value", ((FlagSetting) src).getValue());
            break;
         case SELECT:
            jsonObject.addProperty("value", ((SelectSetting) src).getValue());
            break;
      }
      return jsonObject;
   }
}

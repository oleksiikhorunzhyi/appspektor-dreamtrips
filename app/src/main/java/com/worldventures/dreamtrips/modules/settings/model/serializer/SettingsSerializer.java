package com.worldventures.dreamtrips.modules.settings.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.worldventures.dreamtrips.modules.settings.model.FlagSettings;
import com.worldventures.dreamtrips.modules.settings.model.SelectSettings;
import com.worldventures.dreamtrips.modules.settings.model.Settings;

import java.lang.reflect.Type;

public class SettingsSerializer implements JsonSerializer<Settings> {

    @Override
    public JsonElement serialize(Settings src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.getName());
        switch (src.getType()) {
            case FLAG:
                jsonObject.addProperty("value", ((FlagSettings) src).getValue());
                break;
            case SELECT:
                jsonObject.addProperty("value", ((SelectSettings) src).getValue());
                break;
        }
        return jsonObject;
    }
}

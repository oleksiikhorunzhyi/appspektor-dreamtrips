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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SettingsDeserializer<T extends Setting> implements JsonDeserializer<T> {

    private Gson gson;
    private Map<Setting.Type, Class<? extends Setting>> modelByType = new HashMap<>();

    {
        modelByType.put(Setting.Type.FLAG, FlagSetting.class);
        modelByType.put(Setting.Type.SELECT, SelectSetting.class);
        modelByType.put(Setting.Type.UNKNOWN, Setting.class);
    }

    public SettingsDeserializer() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Setting.Type type = null;
        JsonElement typeElement = json.getAsJsonObject().get("type");
        if (!typeElement.isJsonNull()) {
            type = gson.fromJson(typeElement.getAsJsonPrimitive(), Setting.Type.class);
        }
        if (type == null) type = Setting.Type.UNKNOWN;
        Setting model = gson.fromJson(json, modelByType.get(type));
        return (T) model;
    }
}

package com.worldventures.dreamtrips.modules.settings.model.serializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.modules.settings.model.FlagSettings;
import com.worldventures.dreamtrips.modules.settings.model.SelectSettings;
import com.worldventures.dreamtrips.modules.settings.model.Settings;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SettingsDeserializer<T extends Settings> implements JsonDeserializer<T> {

    private Gson gson;
    private Map<Settings.Type, Class<? extends Settings>> modelByType = new HashMap<>();

    {
        modelByType.put(Settings.Type.FLAG, FlagSettings.class);
        modelByType.put(Settings.Type.SELECT, SelectSettings.class);
        modelByType.put(Settings.Type.UNKNOWN, Settings.class);
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
        Settings.Type type = null;
        JsonElement typeElement = json.getAsJsonObject().get("type");
        if (!typeElement.isJsonNull()) {
            type = gson.fromJson(typeElement.getAsJsonPrimitive(), Settings.Type.class);
        }
        if (type == null) type = Settings.Type.UNKNOWN;
        Settings model = gson.fromJson(json, modelByType.get(type));
        return (T) model;
    }
}

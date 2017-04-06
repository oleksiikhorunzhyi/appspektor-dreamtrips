package com.worldventures.dreamtrips.api.settings.converter;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.api.settings.model.FlagSetting;
import com.worldventures.dreamtrips.api.settings.model.SelectSetting;
import com.worldventures.dreamtrips.api.settings.model.Setting;
import com.worldventures.dreamtrips.api.settings.model.UnknownSetting;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SettingsDeserializer implements JsonDeserializer<Setting> {

    private Gson gson;

    private Map<String, Class<? extends Setting>> modelByName;

    public SettingsDeserializer(Gson gson) {
        this.gson = gson;
        modelByName = new HashMap<String, Class<? extends Setting>>();
        modelByName.put("distance_measurement_unit", SelectSetting.class);
        modelByName.put("receive_friend_request_notifications", FlagSetting.class);
        modelByName.put("receive_new_message_notifications", FlagSetting.class);
        modelByName.put("receive_tagged_on_photo_notifications", FlagSetting.class);
        modelByName.put("receive_merchant_reward_points_notifications", FlagSetting.class);
    }

    @Override
    public Setting deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String name = json.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
        Class<Setting> type = (Class<Setting>) modelByName.get(name);
        if (type != null) {
            return gson.fromJson(json, type);
        } else {
            return UnknownSetting.create(name);
        }
    }

}

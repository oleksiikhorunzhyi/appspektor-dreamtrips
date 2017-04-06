package com.worldventures.dreamtrips.api.settings.converter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.worldventures.dreamtrips.api.settings.model.FlagSetting;
import com.worldventures.dreamtrips.api.settings.model.SelectSetting;
import com.worldventures.dreamtrips.api.settings.model.Setting;

import java.lang.reflect.Type;

public class SettingsSerializer implements JsonSerializer<Setting> {

    private Gson gson;

    public SettingsSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public JsonElement serialize(Setting setting, Type typeOfSrc, JsonSerializationContext context) {
        switch (setting.type()) {
            case FLAG:
                return gson.toJsonTree(setting, FlagSetting.class);
            case SELECT:
                return gson.toJsonTree(setting, SelectSetting.class);
        }
        throw new IllegalArgumentException("Setting is not supported for serialization, type is " + setting.type());
    }
}

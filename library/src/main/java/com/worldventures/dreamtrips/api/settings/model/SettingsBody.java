package com.worldventures.dreamtrips.api.settings.model;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface SettingsBody {

    List<Setting> settings();
}

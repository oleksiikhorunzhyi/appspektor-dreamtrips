package com.worldventures.dreamtrips.api.session.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.settings.model.Setting;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface Session {

    @SerializedName("token")
    String token();
    @SerializedName("sso_token")
    String ssoToken();
    @SerializedName("locale")
    String locale();
    @SerializedName("user")
    Account user();
    @SerializedName("permissions")
    List<Feature> permissions();
    @SerializedName("settings")
    List<Setting> settings();
}

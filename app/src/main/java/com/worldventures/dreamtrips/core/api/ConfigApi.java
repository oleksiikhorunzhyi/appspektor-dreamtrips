package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.common.model.AppConfig;

import retrofit.http.GET;

public interface ConfigApi {
    @GET("/settings_v4.json")
    public AppConfig getConfig();
}

package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;

import retrofit.http.GET;

public interface ConfigApi {
    @GET("/config/settings_v2.json")
    public S3GlobalConfig getConfig();
}

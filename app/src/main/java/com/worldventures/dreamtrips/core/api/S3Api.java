package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;

import retrofit.Callback;
import retrofit.http.GET;

public interface S3Api {
    String DEFAULT_URL = BuildConfig.S3Api;

    @GET("/config/settings_v2.json")
    public void getConfig(Callback<S3GlobalConfig> callback);

    @GET("/config/settings_v2.json")
    public S3GlobalConfig getConfig();

}

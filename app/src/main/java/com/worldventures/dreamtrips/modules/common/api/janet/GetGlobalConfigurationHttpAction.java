package com.worldventures.dreamtrips.modules.common.api.janet;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

@HttpAction
public class GetGlobalConfigurationHttpAction {

    @Url String url = BuildConfig.S3Api + "/settings_v4.json";
    @Response AppConfig appConfig;

    public AppConfig getAppConfig() {
        return appConfig;
    }
}

package com.worldventures.dreamtrips.api.http.provider;

import com.worldventures.dreamtrips.api.http.EnvParams;
import com.worldventures.dreamtrips.api.http.ImmutableEnvParams;

public class SystemEnvProvider implements EnvProvider {
    @Override
    public EnvParams provide() {
        return ImmutableEnvParams.builder()
                .apiUrl(System.getenv("API_URL"))
                .apiVersion(System.getenv("API_VERSION"))
                .apiGlobalConfigUrl(System.getenv("API_GLOBAL_CONFIG_URL"))
                .apiUploaderyUrl(System.getenv("API_UPLOADERY_URL"))
                .appPlatform(System.getenv("CLIENT_PLATFORM"))
                .appVersion(System.getenv("CLIENT_VERSION"))
                .appLanguage(System.getenv("CLIENT_LANGUAGE"))
                .build();
    }
}

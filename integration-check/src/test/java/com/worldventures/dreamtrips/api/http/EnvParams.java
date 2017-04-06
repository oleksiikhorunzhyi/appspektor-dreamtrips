package com.worldventures.dreamtrips.api.http;

import org.immutables.value.Value;

@Value.Immutable
public interface EnvParams {

    String apiUrl();
    String apiVersion();
    String apiGlobalConfigUrl();
    String apiUploaderyUrl();
    String appPlatform();
    String appVersion();
    String appLanguage();

}

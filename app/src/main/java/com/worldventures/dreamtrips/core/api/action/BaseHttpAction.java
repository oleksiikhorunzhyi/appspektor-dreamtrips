package com.worldventures.dreamtrips.core.api.action;

import android.annotation.SuppressLint;
import android.os.Build;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;

import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.Response;

public class BaseHttpAction {
    @RequestHeader("Accept") public String acceptHeader = "application/com.dreamtrips.api+json;version=" + BuildConfig.API_VERSION;
    @SuppressLint("DefaultLocale")
    @RequestHeader("DT-App-Platform") public String platformHeader = String.format("android-%d", Build.VERSION.SDK_INT);
    @RequestHeader("Accept-Language") public String languageHeader;
    @RequestHeader("DT-App-Version") public String appVersionHeader;
    @Response(min = 400) public ErrorResponse errorResponse;

    public String getPlatformHeader() {
        return platformHeader;
    }

    public String getAppVersionHeader() {
        return appVersionHeader;
    }

    public void setAppVersionHeader(String appVersionHeader) {
        this.appVersionHeader = appVersionHeader;
    }

    public String getLanguageHeader() {
        return languageHeader;
    }

    public void setLanguageHeader(String languageHeader) {
        this.languageHeader = languageHeader;
    }

    public String getAcceptHeader() {
        return acceptHeader;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}

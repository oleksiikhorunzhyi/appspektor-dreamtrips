package com.worldventures.dreamtrips.api.api_common;

import com.worldventures.dreamtrips.api.api_common.error.ErrorResponse;

import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Status;

public abstract class BaseHttpAction {
    @RequestHeader("Accept")
    String acceptHeader;
    @RequestHeader("DT-App-Platform")
    String appPlatformHeader;
    @RequestHeader("DT-App-Version")
    String appVersionHeader;
    @RequestHeader("Accept-Language")
    String appLanguageHeader;
    //
    @Status
    int statusCode;
    @Response(min = 400, max = 499)
    ErrorResponse errorResponse;

    ///////////////////////////////////////////////////////////////////////////
    // Getters & Setters
    ///////////////////////////////////////////////////////////////////////////

    public String getAcceptHeader() {
        return acceptHeader;
    }

    public void setApiVersionForAccept(String version) {
        acceptHeader = "application/com.dreamtrips.api+json;version=" + version;
    }

    public String getAppPlatformHeader() {
        return appPlatformHeader;
    }

    public void setAppPlatformHeader(String appPlatformHeader) {
        this.appPlatformHeader = appPlatformHeader;
    }

    public String getAppVersionHeader() {
        return appVersionHeader;
    }

    public void setAppVersionHeader(String appVersionHeader) {
        this.appVersionHeader = appVersionHeader;
    }

    public String getAppLanguageHeader() {
        return appLanguageHeader;
    }

    public void setAppLanguageHeader(String appLanguageHeader) {
        this.appLanguageHeader = appLanguageHeader;
    }

    public int statusCode() {
        return statusCode;
    }

    public ErrorResponse errorResponse() {
        return errorResponse;
    }
}

package com.messenger.util;

import com.crashlytics.android.Crashlytics;
import com.worldventures.dreamtrips.core.api.error.DtApiException;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;

import org.apache.commons.lang3.StringUtils;

import retrofit.RetrofitError;

public final class CrashlyticsTracker {

    public static void trackError(Throwable throwable) {
        Crashlytics.logException(throwable);
    }


    public static void trackError(RetrofitError throwable) {
        Crashlytics.log("Server error");
        Crashlytics.log("url: " + throwable.getResponse().getUrl());
        Crashlytics.log("status: " + throwable.getResponse().getStatus());
        Crashlytics.log("reason: " + throwable.getResponse().getReason());
        Crashlytics.log("headers: " + StringUtils.join(throwable.getResponse().getHeaders()));
        Crashlytics.log("body: " + throwable.getBody());
        Crashlytics.logException(new DtApiException(getErrorMessageForCrashlytics(throwable),
                (ErrorResponse) throwable.getBodyAs(ErrorResponse.class), throwable.getResponse().getStatus()));

    }


    private static String getErrorMessageForCrashlytics(RetrofitError cause) {
        return String.format("%s throws api exception", cause.getResponse().getUrl());
    }
}
package com.messenger.util;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.worldventures.dreamtrips.core.api.error.DtApiException;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;

import java.util.Set;

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
        Crashlytics.log("headers: " + TextUtils.join(", ", throwable.getResponse().getHeaders()));
        Crashlytics.log("body: " + throwable.getBody());
        Crashlytics.logException(new DtApiException(getErrorMessageForCrashlytics(throwable),
                (ErrorResponse) throwable.getBodyAs(ErrorResponse.class), throwable.getResponse().getStatus()));

    }

    public static void trackErrorWithParams(Throwable throwable, ArrayMap<String, ?> paramsMap) {
        Set<String> keys = paramsMap.keySet();
        for(String key : keys) {
            Crashlytics.log(key + " : " + paramsMap.get(key));
        }

        Crashlytics.logException(throwable);
    }

    private static String getErrorMessageForCrashlytics(RetrofitError cause) {
        return String.format("%s throws api exception", cause.getResponse().getUrl());
    }
}
package com.messenger.util;

import com.crashlytics.android.Crashlytics;

public final class CrashlyticsTracker {

    public static void trackError(Throwable throwable){
        Crashlytics.logException(throwable);
    }

}

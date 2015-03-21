package com.worldventures.dreamtrips.core.utils.events;

import android.util.Log;

public class UpdateUserInfoEvent {
    public UpdateUserInfoEvent() {
        Log.i("UpdateUserInfoEvent", "Send from " + Thread.currentThread().getStackTrace()[2]);
    }
}

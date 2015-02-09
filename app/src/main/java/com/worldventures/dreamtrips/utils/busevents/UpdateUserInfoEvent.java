package com.worldventures.dreamtrips.utils.busevents;

import android.util.Log;

public class UpdateUserInfoEvent {
    public UpdateUserInfoEvent() {
        Log.i("UpdateUserInfoEvent", "Send from " + Thread.currentThread().getStackTrace()[2]);
    }
}

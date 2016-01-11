package com.worldventures.dreamtrips.core.utils;

import android.content.Intent;

public class ActivityResultDelegate {

    int requestCode;
    int resultCode;
    Intent data;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Intent getData() {
        return data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    /**
     * Clear data stored in delegate from previous activation - to ensure it won't <br />
     * fire again with outdated request/result code or data
     */
    public void clear() {
        onActivityResult(Integer.MIN_VALUE, Integer.MIN_VALUE, null);
    }
}

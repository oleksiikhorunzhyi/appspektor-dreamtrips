package com.worldventures.dreamtrips.core.api.request.bucketlist;

import android.util.Log;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;

public class DeleteBucketItem extends DreamTripsRequest<JsonObject> {
    private int id;
    private long delay;
    private boolean isCanceled = false;

    public DeleteBucketItem(int id, long delay) {
        super(JsonObject.class);
        this.delay = delay;
        this.id = id;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    @Override
    public JsonObject loadDataFromNetwork() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Log.e(DreamTripsRequest.class.getName(), "", e);
        }

        if (isCanceled) {
            return new JsonObject();
        } else {
            Log.d("TAG_BucketListPM", "Sending delete item event");
            return getService().deleteItem(id);
        }
    }
}

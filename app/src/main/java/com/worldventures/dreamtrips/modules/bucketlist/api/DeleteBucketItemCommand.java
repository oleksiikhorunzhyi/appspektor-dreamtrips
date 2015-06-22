package com.worldventures.dreamtrips.modules.bucketlist.api;

import android.util.Log;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;

public class DeleteBucketItemCommand extends Command<JsonObject> {
    private int id;
    private long delay;
    private volatile boolean canceled;

    public DeleteBucketItemCommand(int id, long delay) {
        super(JsonObject.class);
        this.delay = delay;
        this.id = id;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    @Override
    public JsonObject loadDataFromNetwork() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Log.e(DreamTripsRequest.class.getName(), "", e);
        }

        if (canceled) {
            return new JsonObject();
        } else {
            Log.d("TAG_BucketListPM", "Sending delete item event");
            return getService().deleteItem(id);
        }
    }
}

package com.worldventures.dreamtrips.core.api.spice;

import com.octo.android.robospice.request.SpiceRequest;

public class PostDelayRequest<RESULT> extends SpiceRequest<RESULT> {


    private DreamTripsRequest<RESULT> request;
    private long postDelay;

    public PostDelayRequest(Class<RESULT> clazz, DreamTripsRequest<RESULT> request, long postDelay) {
        super(clazz);
        this.request = request;
        this.postDelay = postDelay;
    }

    @Override
    public RESULT loadDataFromNetwork() throws Exception {
        Thread.sleep(postDelay);
        if (isCancelled()) {
            return null;
        }
        return request.loadDataFromNetwork();
    }
}

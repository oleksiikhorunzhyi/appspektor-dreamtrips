package com.worldventures.dreamtrips.core.utils;

import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

import java.util.ArrayList;

public abstract class DreamSpiceAdapterController<BaseItemClass>
        extends RoboSpiceAdapterController<DreamSpiceManager, BaseItemClass> {

    private DreamSpiceManager.SuccessListener<ArrayList<BaseItemClass>> nextRequestSuccessListener =
            this::onNextItemsLoaded;

    private DreamSpiceManager.SuccessListener<ArrayList<BaseItemClass>> baseRequestSuccessListener =
            this::onRefresh;

    private DreamSpiceManager.FailureListener baseRequestFailureListener =
            this::onFailure;

    @Override
    protected void executeNextRequest(SpiceRequest<ArrayList<BaseItemClass>> request) {
        if (spiceManager != null)
            spiceManager.execute(request, nextRequestSuccessListener, baseRequestFailureListener);
    }

    private SpiceRequest<ArrayList<BaseItemClass>> request;

    public void cancelRequest() {
        if (request != null && !request.isCancelled()) request.cancel();
    }

    @Override
    protected void executeBaseRequest(SpiceRequest<ArrayList<BaseItemClass>> request) {
        this.request = request;
        if (spiceManager != null)
            spiceManager.execute(request,
                    baseRequestSuccessListener,
                    baseRequestFailureListener);
    }

}
